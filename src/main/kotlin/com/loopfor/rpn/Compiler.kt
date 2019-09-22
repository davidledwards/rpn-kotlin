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
    try {
        Compiler.main(args)
    } catch (e: Exception) {
        println(e.message)
    }
}

object Compiler {
    fun main(args: Array<String>) {
        val ins = InputSequence(System.`in`)
        when (val arg = args.firstOrNull()) {
            "-help" -> {
                println("usage: rpnc [options]")
                println("  Compile expression from stdin and emit instructions to stdout.")
                println("  -t  tokenize only")
                println("  -p  parse only")
                println("  -o  optimize")
            }
            "-t" -> {
                val tokens = Lexer(ins)
                tokens.forEach { println(it) }
            }
            "-p" -> {
                val ast = Parser(Lexer(ins))
                println(ast.format())
            }
            "-o" -> {
                val lines = Emitter(Optimizer(Generator(Parser(Lexer(ins)))))
                lines.forEach { println(it) }
            }
            null -> {
                val lines = Emitter(Generator(Parser(Lexer(ins))))
                lines.forEach { println(it) }
            }
            else ->
                println("$arg: unrecognized option")
        }
    }
}
