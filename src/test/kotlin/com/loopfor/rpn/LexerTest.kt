/*
 * Copyright 2019 David Edwards
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.loopfor.rpn

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class LexerTest {
    @Test
    fun `validate tokens in randomized expressions`() {
        (1..100).forEach {
            val (expr, tokens) = Expression.generate()
            try {
                val ts = Lexer(expr)
                for ((x, y) in ts.zip(tokens.asSequence()))
                    assertEquals(x.lexeme, y.lexeme)
            } catch (e: Exception) {
                fail(e.message)
            }
        }
    }

    @Test
    fun `malformed numbers`() {
        val tests = listOf(
            ".",
            ".1",
            "1.",
            "1.2."
            )

        for (expr in tests) {
            try {
                Lexer(expr).count()
                fail<Unit>(expr)
            } catch (_: Exception) {
            }
        }
    }
}
