/*
 * This file is part of Flex. An advanced text processing library for Mindustry plugins.
 *
 * MIT License
 *
 * Copyright (c) 2024 Xpdustry
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.xpdustry.flex.message

import com.xpdustry.distributor.api.Distributor
import com.xpdustry.distributor.api.annotation.EventHandler
import com.xpdustry.distributor.api.audience.Audience
import com.xpdustry.distributor.api.key.MutableKeyContainer
import com.xpdustry.distributor.api.key.StandardKeys
import com.xpdustry.distributor.api.player.MUUID
import com.xpdustry.distributor.api.plugin.MindustryPlugin
import com.xpdustry.distributor.api.util.Priority
import com.xpdustry.flex.FlexConfig
import com.xpdustry.flex.FlexKeys
import com.xpdustry.flex.FlexListener
import com.xpdustry.flex.FlexScope
import com.xpdustry.flex.placeholder.PlaceholderContext
import com.xpdustry.flex.placeholder.PlaceholderPipeline
import com.xpdustry.flex.processor.AbstractPriorityProcessorPipeline
import java.util.concurrent.CompletableFuture
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.future.await
import kotlinx.coroutines.future.future
import mindustry.Vars
import mindustry.game.EventType
import org.slf4j.LoggerFactory

internal class MessagePipelineImpl(
    plugin: MindustryPlugin,
    private val placeholders: PlaceholderPipeline,
    private var config: MessageConfig,
) :
    AbstractPriorityProcessorPipeline<MessageContext, CompletableFuture<String>>(plugin, "message"),
    MessagePipeline,
    FlexListener {
    private val foo = mutableSetOf<MUUID>()

    override fun onPluginInit() {
        Vars.netServer.addPacketHandler("fooCheck") { player, _ -> foo += MUUID.from(player) }

        register("foo_sign_strip", Priority.HIGH) { context ->
            var msg = context.message
            // https://github.com/mindustry-antigrief/mindustry-client/blob/23025185c20d102f3fbb9d9a4c20196cc871d94b/core/src/mindustry/client/communication/InvisibleCharCoder.kt#L14
            if (
                config.fooClientCompatibility &&
                    context.sender.isFooClient() &&
                    msg.takeLast(2).all { (0xF80 until 0x107F).contains(it.code) }
            ) {
                msg = msg.dropLast(2)
            }
            CompletableFuture.completedFuture(msg)
        }
    }

    override fun onFlexConfigReload(config: FlexConfig) {
        this.config = config.messages
        logger.info("Reloaded message pipeline config")
    }

    override fun pump(context: MessageContext) =
        FlexScope.future {
            var result = context.message
            for (processor in processors.values.sorted()) {
                result =
                    try {
                        processor.process(context.copy(message = result)).await()
                    } catch (error: Throwable) {
                        plugin.logger.error(
                            "Error while processing message of {} to {}",
                            context.sender.metadata[StandardKeys.NAME] ?: "Unknown",
                            context.target.metadata[StandardKeys.NAME] ?: "Unknown",
                            error,
                        )
                        result
                    }
                if (result.isBlank()) break
            }
            result
        }

    override fun broadcast(
        sender: Audience,
        target: Audience,
        message: String,
        template: String,
    ): CompletableFuture<Void?> =
        FlexScope.future {
            target.audiences
                .map { target ->
                    FlexScope.async {
                        val processed = pump(MessageContext(sender, target, message)).await()
                        if (processed.isBlank()) {
                            return@async
                        }

                        val formatted =
                            placeholders.pump(
                                PlaceholderContext(
                                    sender,
                                    "%template:$template%",
                                    MutableKeyContainer.create().apply { set(FlexKeys.MESSAGE, processed) },
                                )
                            )

                        if (formatted.isBlank()) {
                            return@async
                        }

                        target.sendMessage(
                            Distributor.get().mindustryComponentDecoder.decode(formatted),
                            Distributor.get().mindustryComponentDecoder.decode(processed),
                            if (config.fooClientCompatibility && target.isFooClient()) Audience.empty() else sender,
                        )
                    }
                }
                .awaitAll()
            null
        }

    @EventHandler
    internal fun onPlayerQuit(event: EventType.PlayerLeave) {
        foo -= MUUID.from(event.player)
    }

    private fun Audience.isFooClient() = metadata[StandardKeys.MUUID]?.let(foo::contains) ?: false

    companion object {
        private val logger = LoggerFactory.getLogger(MessagePipelineImpl::class.java)
    }
}
