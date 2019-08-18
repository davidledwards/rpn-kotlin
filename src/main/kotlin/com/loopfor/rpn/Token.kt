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

/**
 * Represents a token recognized in the input stream.
 * 
 * Recognized tokens defined as regular expressions:
 * ```
 * Plus = +
 * Minus = -
 * Star = *
 * Slash = /
 * Percent = %
 * Caret = ^
 * LeftParen = (
 * RightParen = )
 * Min = min
 * Max = max
 * Symbol = [A-Za-z]+
 * Number = ([0-9]+)|([0-9]+\.[0-9]+)
 * EOS = <end of stream>
 * ```
 */
sealed class Token(val lexeme: String) {
    override fun toString(): String = "Token($lexeme)"
}

class PlusToken : Token("+")
class MinusToken : Token("-")
class StarToken : Token("*")
class SlashToken : Token("/")
class PercentToken : Token("%")
class CaretToken : Token("^")
class LeftParenToken : Token("(")
class RightParenToken : Token(")")
class MinToken : Token("min")
class MaxToken : Token("max")
class SymbolToken(lexeme: String) : Token(lexeme)
class NumberToken(lexeme: String) : Token(lexeme)
class EOSToken : Token("<EOS>")

object Tokens {
    val EOS = EOSToken()

    val reserved: Map<String, Token> = listOf(
        PlusToken(),
        MinusToken(),
        StarToken(),
        SlashToken(),
        PercentToken(),
        CaretToken(),
        LeftParenToken(),
        RightParenToken(),
        MinToken(),
        MaxToken()
    ).map { it.lexeme to it }.toMap()

    val simple: Map<Char, Token> =
        reserved.filter { (k, _) -> k.length == 1 }.mapKeys { (k, _) -> k[0] }

    val symbols: Map<String, Token> = listOf(
        "^", "min", "max"
    ).map { it to reserved.getValue(it) }.toMap()
}
