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

import java.lang.System.currentTimeMillis
import kotlin.random.Random
import kotlin.random.nextInt

object Expression {
    private val r = Random(currentTimeMillis())

    fun generate(ws: () -> String): Pair<String, List<Token>> {
        fun expression(operand: () -> List<Token>): List<Token> {
            return (0 until r.nextInt(0 until 4)).fold(operand()) {
                e, _ -> e + Operator.generate() + operand()
            }
        }

        fun operand(): List<Token> = when (r.nextInt(0 until 3)) {
            0 -> listOf(Num.generate())
            1 -> listOf(Sym.generate())
            else -> listOf(LeftParenToken()) + expression(::operand) + RightParenToken()
        }

        val tokens = expression(::operand)
        val expr = tokens.map { it.lexeme + ws() }.joinToString(separator = "")
        return Pair(expr, tokens)
    }

    object Sym {
        private val chars = ('A'..'Z') + ('a'..'z')
        private val range = 0 until chars.size

        fun generate() = SymbolToken("${chars.get(r.nextInt(range))}${chars.get(r.nextInt(range))}")
    }

    object Num {
        fun generate() = NumberToken(String.format("%2.2f", r.nextDouble()))
    }

    object Space {
        private val chars = listOf(" ", "\n", "\r", "\t", "\u000c")
        private val range = 0 until chars.size

        val space = " "
        fun generate() = chars.get(r.nextInt(range))
    }

    object Operator {
        private val operators = listOf<Token>(
            PlusToken(),
            MinusToken(),
            StarToken(),
            SlashToken(),
            PercentToken(),
            CaretToken(),
            MinToken(),
            MaxToken()
        )
        private val range = 0 until operators.size

        fun generate() = operators.get(r.nextInt(range))
    }
}
