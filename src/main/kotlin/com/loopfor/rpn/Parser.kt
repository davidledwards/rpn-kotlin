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
        return SymbolAST("wow")
    }
}
