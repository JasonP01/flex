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
package com.xpdustry.flex.translator

import com.xpdustry.flex.FlexConfig
import com.xpdustry.flex.FlexListener
import java.util.Locale
import kotlin.time.toJavaDuration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import org.slf4j.LoggerFactory

internal class TranslatorProxy(config: TranslatorConfig.Backend) : Translator, FlexListener {
    @Volatile private var translator = createTranslator(config)

    @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
    override fun translate(text: String, source: Locale, target: Locale) = translator.translate(text, source, target)

    override fun translateDetecting(text: String, source: Locale, target: Locale) =
        translator.translateDetecting(text, source, target)

    override fun onFlexConfigReload(config: FlexConfig) {
        translator = createTranslator(config.translator.backend)
        logger.info("Translator reloaded, replaced with {}", translator.javaClass.simpleName)
    }

    private fun createTranslator(config: TranslatorConfig.Backend): Translator {
        val executor = Dispatchers.IO.asExecutor()
        return when (config) {
            is TranslatorConfig.Backend.None -> Translator.noop()
            is TranslatorConfig.Backend.LibreTranslate ->
                Translator.libreTranslate(config.ltEndpoint, executor, config.ltApiKey?.value)
            is TranslatorConfig.Backend.DeepL -> Translator.deepl(config.deeplApiKey.value, executor)
            is TranslatorConfig.Backend.GoogleBasic -> Translator.googleBasic(config.googleBasicApiKey.value, executor)
            is TranslatorConfig.Backend.Rolling ->
                Translator.rolling(config.translators.map(::createTranslator), createTranslator(config.fallback))
            is TranslatorConfig.Backend.Caching ->
                Translator.caching(
                    createTranslator(config.cachingTranslator),
                    executor,
                    config.maximumSize,
                    config.successRetention.toJavaDuration(),
                    config.failureRetention.toJavaDuration(),
                )
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(TranslatorProxy::class.java)
    }
}
