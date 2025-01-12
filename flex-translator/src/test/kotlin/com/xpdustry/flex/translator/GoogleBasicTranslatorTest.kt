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

import java.util.Locale
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable

class GoogleBasicTranslatorTest {
    @EnabledIfEnvironmentVariable(named = API_KEY_ENV, matches = ".+")
    @Test
    fun test() {
        val translator = assertDoesNotThrowsAndReturns {
            GoogleBasicTranslator(System.getenv(API_KEY_ENV), Runnable::run)
        }
        Assertions.assertTrue(translator.supported.isNotEmpty())
        Assertions.assertDoesNotThrow {
            val result = translator.translateDetecting("Bonjour", Locale.FRENCH, Locale.ENGLISH).join()
            Assertions.assertEquals(Locale.FRENCH.language, result.detected?.language)
        }
        Assertions.assertDoesNotThrow {
            val result = translator.translateDetecting("Bonjour", Translator.AUTO_DETECT, Locale.ENGLISH).join()
            Assertions.assertEquals(Locale.FRENCH.language, result.detected?.language)
        }
    }

    companion object {
        private const val API_KEY_ENV = "FLEX_TEST_TRANSLATOR_GOOGLE_BASIC_API_KEY"
    }
}
