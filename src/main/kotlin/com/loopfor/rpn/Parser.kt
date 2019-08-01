/*
 * Copyright 2015 David Edwards
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
 * A recursive-descent parser that transforms a stream of tokens into a syntax tree.
 * 
 * Grammar:
 * {{{
 * p0 ::= <p2> <p1>
 * p1 ::= '+' <p2> <p1>
 *    ::= '-' <p2> <p1>
 *    ::= e
 * p2 ::= <p4> <p3>
 * p3 ::= '*' <p4> <p3>
 *    ::= '/' <p4> <p3>
 *    ::= '%' <p4> <p3>
 *    ::= '^' <p4> <p3>
 *    ::= e
 * p4 ::= <p6> <p5>
 * p5 ::= 'min' <p6> <p5>
 *    ::= 'max' <p6> <p5>
 *    ::= e
 * p6 ::= '(' <p0> ')'
 *    ::= <symbol>
 *    ::= <number>
 * }}}
 */
interface Parser : (Sequence<Token>) -> AST {
    companion object {
        fun create(): Parser = BasicParser()
        fun create(tokens: Sequence<Token>): AST = create()(tokens)
    }
}

private class BasicParser : Parser {
    override fun invoke(ins: Sequence<Token>): AST {
        val (ast, rest) = p0(ins)
        val t = rest.firstOrNull()
        return when (t) {
            is Token -> throw Exception("${t.lexeme}: expecting ${EOSToken.lexeme}")
            else -> ast
        }
    }

    /**
     * p0 ::= <p2> <p1>
     */
    private fun p0(ins: Sequence<Token>): Pair<AST, Sequence<Token>> {
        val (l, rest) = p2(ins)
        return p1(l, rest)
    }

    /**
     * p1 ::= '+' <p2> <p1>
     *    ::= '-' <p2> <p1>
     *    ::= e
     */
    private tailrec fun p1(l: AST, ins: Sequence<Token>): Pair<AST, Sequence<Token>> {
        return when (ins.firstOrNull()) {
            is PlusToken -> {
                val (r, rest) = p2(ins.drop(1))
                p1(AddAST(l, r), rest)
            }
            is MinusToken -> {
                val (r, rest) = p2(ins.drop(1))
                p1(SubtractAST(l, r), rest)
            }
            else ->
                Pair(l, ins)
        }
    }

    /**
     * p2 ::= <p4> <p3>
     */
    private fun p2(ins: Sequence<Token>): Pair<AST, Sequence<Token>> {
        val (l, rest) = p4(ins)
        return p3(l, rest)
    }

    /**
     * p3 ::= '*' <p4> <p3>
     *    ::= '/' <p4> <p3>
     *    ::= '%' <p4> <p3>
     *    ::= '^' <p4> <p3>
     *    ::= e
     */
    private tailrec fun p3(l: AST, ins: Sequence<Token>): Pair<AST, Sequence<Token>> {
        return when (ins.firstOrNull()) {
            is StarToken -> {
                val (r, rest) = p4(ins.drop(1))
                p3(MultiplyAST(l, r), rest)
            }
            is SlashToken -> {
                val (r, rest) = p4(ins.drop(1))
                p3(DivideAST(l, r), rest)
            }
            is PercentToken -> {
                val (r, rest) = p4(ins.drop(1))
                p3(ModuloAST(l, r), rest)
            }
            is CaretToken -> {
                val (r, rest) = p4(ins.drop(1))
                p3(PowerAST(l, r), rest)
            }
            else ->
                Pair(l, ins)
        }
    }

    /**
     * p4 ::= <p6> <p5>
     */
    private fun p4(ins: Sequence<Token>): Pair<AST, Sequence<Token>> {
        val (l, rest) = p6(ins)
        return p5(l, rest)
    }

    /**
     * p5 ::= 'min' <p6> <p5>
     *    ::= 'max' <p6> <p5>
     *    ::= e
     */
    private tailrec fun p5(l: AST, ins: Sequence<Token>): Pair<AST, Sequence<Token>> {
        return when (ins.firstOrNull()) {
            is MinToken -> {
                val (r, rest) = p6(ins.drop(1))
                p5(MinAST(l, r), rest)
            }
            is MaxToken -> {
                val (r, rest) = p6(ins.drop(1))
                p5(MaxAST(l, r), rest)
            }
            else ->
                Pair(l, ins)
        }
    }

    /**
     * p6 ::= '(' <p0> ')'
     *    ::= <symbol>
     *    ::= <number>
     */
    private fun p6(ins: Sequence<Token>): Pair<AST, Sequence<Token>> {
        val t = ins.firstOrNull()
        return when (t) {
            is LeftParenToken -> {
                val (ast, rest) = p0(ins.drop(1))
                Pair(ast, confirm(rest, RightParenToken))
            }
            is SymbolToken ->
                Pair(SymbolAST(t.lexeme), ins.drop(1))
            is NumberToken ->
                Pair(NumberAST(t.lexeme.toDouble()), ins.drop(1))
            else -> {
                val lexeme = (t ?: EOSToken).lexeme
                throw Exception("$lexeme: expecting '(', <symbol> or <number>")
            }
        }
    }

    private fun confirm(ins: Sequence<Token>, token: Token): Sequence<Token> {
        val t = ins.firstOrNull()
        return when (t) {
            is Token, token -> ins.drop(1)
            else -> {
                val lexeme = (t ?: EOSToken).lexeme
                throw Exception("$lexeme: expecting '${token.lexeme}'")
            }
        }
    }
}
