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
 * A lexical analyzer that transforms a stream of characters into a stream of tokens.
 * 
 * Tokens must either be delimited by one or more whitespace characters, or be clearly
 * distinguishable from each other if not separated by whitespace.
 */
interface Lexer : (Sequence<Char>) -> Sequence<Token> {
    companion object {
        fun create(): Lexer = BasicLexer()
        fun create(ins: Sequence<Char>): Sequence<Token> = create()(ins)
        fun create(ins: String): Sequence<Token> = create()(ins.asSequence())
    }
}

private class BasicLexer : Lexer {
    override fun invoke(ins: Sequence<Char>): Sequence<Token> {
        fun tokens(ins: Sequence<Char>): Sequence<Token> {
            val (token, _ins) = tokenize(ins)
            return when (token) {
                Tokens.EOS -> emptySequence<Token>()
                else -> sequenceOf<Token>(token) + tokens(_ins)
            }
        }
        return tokens(ins)
    }

    private tailrec fun tokenize(ins: Sequence<Char>): Pair<Token, Sequence<Char>> {
        val c = ins.firstOrNull()
        return when (c) {
            in WHITESPACE ->
                tokenize(ins.drop(1))
            in Tokens.simple ->
                Pair(Tokens.simple.getValue(c!!), ins.drop(1))
            in DIGITS ->
                readNumber(ins.drop(1), "$c")
            in LETTERS ->
                readSymbol(ins.drop(1), "$c")
            null ->
                Pair(Tokens.EOS, ins)
            else ->
                throw Exception("$c: unrecognized character")
        }
    }

    private tailrec fun readNumber(ins: Sequence<Char>, lexeme: String): Pair<Token, Sequence<Char>> {
        val c = ins.firstOrNull()
        return when (c) {
            '.' ->
                if (c in lexeme)
                    throw Exception("$lexeme: malformed number")
                else
                    readNumber(ins.drop(1), lexeme + '.')
            in DIGITS ->
                readNumber(ins.drop(1), lexeme + c)
            else ->
                if (lexeme.last() == '.')
                    throw Exception("$lexeme: malformed number")
                else
                    Pair(NumberToken(lexeme), ins)
        }
    }

    private tailrec fun readSymbol(ins: Sequence<Char>, lexeme: String): Pair<Token, Sequence<Char>> {
        val c = ins.firstOrNull()
        return if (c in LETTERS)
            readSymbol(ins.drop(1), lexeme + c)
        else
            Pair(Tokens.symbols.get(lexeme) ?: SymbolToken(lexeme), ins)
    }

    private val DIGITS = ('0'..'9').toSet()
    private val LETTERS = (('A'..'Z') + ('a'..'z')).toSet()
    private val WHITESPACE = setOf(' ', '\n', '\r', '\t', '\u000c')
}
