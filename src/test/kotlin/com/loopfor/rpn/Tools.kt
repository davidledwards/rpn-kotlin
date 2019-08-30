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

fun main(args: Array<String>) {
    Tools.parserTests(100)
    Tools.generatorTests(100)
    Tools.optimizerTests(100)
}

/**
 * Tools for automatically generating random conformant expressions.
 * 
 * Note that these expressions are generated for testing purposes only and may
 * appear to be senseless when visually inspected. However, they do conform to
 * the specified grammar and are sufficient for detecting regressions.
 * 
 * The output of each test generation is formatted such that it can be
 * literally cut and pasted into the unit test itself.
 */
object Tools {
    /**
     * Produces a random expression and its corresponding AST.
     */
    fun parserTests(count: Int) {
        println("${tab(1)}private val tests = listOf<Pair<String, AST>>(")
        (1..count).forEach { n ->
            val (expr, _) = Expression.generate()
            println("""${tab(2)}Pair("$expr",""")
            val ast = Parser.create(Lexer.create(expr))

            fun emit(ast: AST, depth: Int) {
                fun printAST(name: String, l: AST, r: AST) {
                    println("${tab(depth)}$name(")
                    emit(l, depth + 1)
                    println(",")
                    emit(r, depth + 1)
                    print("\n${tab(depth)})")
                }
                when (ast) {
                    is SymbolAST ->
                        print("""${tab(depth)}SymbolAST("${ast.name}")""")
                    is NumberAST ->
                        print("""${tab(depth)}NumberAST(${ast.value})""")
                    is AddAST ->
                        printAST("AddAST", ast.l, ast.r)
                    is SubtractAST ->
                        printAST("SubtractAST", ast.l, ast.r)
                    is MultiplyAST ->
                        printAST("MultiplyAST", ast.l, ast.r)
                    is DivideAST ->
                        printAST("DivideAST", ast.l, ast.r)
                    is ModuloAST ->
                        printAST("ModuloAST", ast.l, ast.r)
                    is PowerAST ->
                        printAST("PowerAST", ast.base, ast.exp)
                    is MinAST ->
                        printAST("MinAST", ast.l, ast.r)
                    is MaxAST ->
                        printAST("MaxAST", ast.l, ast.r)
                }
            }

            emit(ast, 3)
            print(")")
            if (n < count) println(",") else println(")")
        }
    }

    /**
     * Produces a random expression and its corresponding unoptimized instruction
     * sequence.
     */
    fun generatorTests(count: Int) {
    }

    /**
     * Produces a sequence of instructions with a computed value.
     * 
     * The computed value is possible due to a method of assigning values to each
     * symbol during evalation using a deterministic hash. Since symbol names are
     * stable, an optimization of the instruction sequence should produce the
     * same value.
     * 
     * Note that in some cases, the precomputed value is `NaN` or `Infinity`, which
     * should be removed from the set before use in unit tests.
     */
    fun optimizerTests(count: Int) {
    }

    fun hash(name: String): Double {
        return name.fold(0.0) { h, c -> h + c.toDouble() / 100.0 } / 10.0
    }

    private fun tab(n: Int) = " ".repeat(n * 4)
}
