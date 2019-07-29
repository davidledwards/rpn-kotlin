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
 * {{{
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
 * }}}
 */
sealed class Token(val type: String, val lexeme: String) {
    override fun toString(): String = "$type($lexeme)"
}

object PlusToken : Token("PlusToken", "+")
object MinusToken : Token("MinusToken", "-")
object StarToken : Token("StarToken", "*")
object SlashToken : Token("SlashToken", "/")
object PercentToken : Token("PercentToken", "%")
object CaretToken : Token("CaretToken", "^")
object LeftParenToken : Token("LeftParenToken", "(")
object RightParenToken : Token("RightParenToken", ")")
object MinToken : Token("MinToken", "min")
object MaxToken : Token("MaxToken", "max")

class SymbolToken(lexeme: String) : Token("SymbolToken", lexeme)
class NumberToken(lexeme: String) : Token("NumberToken", lexeme)

object EOSToken : Token("EOSToken", "<EOS>")

val SIMPLE_TOKENS: Map<Char, Token> = mapOf(
    PlusToken.lexeme[0] to PlusToken,
    MinusToken.lexeme[0] to MinusToken,
    StarToken.lexeme[0] to StarToken,
    SlashToken.lexeme[0] to SlashToken,
    PercentToken.lexeme[0] to PercentToken,
    CaretToken.lexeme[0] to CaretToken,
    LeftParenToken.lexeme[0] to LeftParenToken,
    RightParenToken.lexeme[0] to RightParenToken
)

val SYMBOL_TOKENS: Map<String, Token> = mapOf(
    CaretToken.lexeme to CaretToken,
    MinToken.lexeme to MinToken,
    MaxToken.lexeme to MaxToken
)
