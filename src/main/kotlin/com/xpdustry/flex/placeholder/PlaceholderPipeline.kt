/*
 * This file is part of FlexPlugin. A chat management plugin for Mindustry.
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
package com.xpdustry.flex.placeholder

import com.xpdustry.distributor.api.audience.Audience
import com.xpdustry.distributor.api.key.Key
import com.xpdustry.distributor.api.key.KeyContainer
import com.xpdustry.flex.processor.ProcessorPipeline

public data class PlaceholderContext
    @JvmOverloads
    constructor(
        val subject: Audience,
        val query: String,
        val arguments: KeyContainer = KeyContainer.empty(),
    )

public enum class PlaceholderMode {
    TEXT,
    PRESET,
}

public interface PlaceholderPipeline : ProcessorPipeline<PlaceholderContext, String> {
    override fun pump(context: PlaceholderContext): String = pump(context, PlaceholderMode.TEXT)

    public fun pump(
        context: PlaceholderContext,
        mode: PlaceholderMode,
    ): String

    public companion object {
        @JvmStatic
        public val MESSAGE: Key<String> = Key.of("flex", "message", String::class.java)

        @JvmStatic
        public val TRANSLATED_MESSAGE: Key<String> = Key.of("flex", "translated_message", String::class.java)
    }
}
