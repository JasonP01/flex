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

import com.xpdustry.distributor.api.audience.Audience
import com.xpdustry.flex.placeholder.template.TemplateManager
import com.xpdustry.flex.processor.PriorityProcessorPipeline
import java.util.concurrent.CompletableFuture

public data class MessageContext
@JvmOverloads
constructor(
    val sender: Audience,
    val target: Audience,
    val message: String,
    @get:JvmName("isFiltering") val filter: Boolean = false,
    val kind: Kind = Kind.CHAT,
) {
    public enum class Kind {
        CHAT,
        COMMAND,
    }
}

public interface MessagePipeline : PriorityProcessorPipeline<MessageContext, CompletableFuture<String>> {
    public fun broadcast(sender: Audience, target: Audience, message: String): CompletableFuture<Void?> =
        broadcast(sender, target, message, TemplateManager.CHAT_TEMPLATE_NAME)

    public fun broadcast(
        sender: Audience,
        target: Audience,
        message: String,
        template: String,
    ): CompletableFuture<Void?>
}
