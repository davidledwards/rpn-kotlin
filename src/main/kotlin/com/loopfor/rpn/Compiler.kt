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

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader

fun main(args: Array<String>) {
    Compiler.main(args)
}

object Compiler {
    fun main(args: Array<String>) = try {
        val arg = args.firstOrNull()
        val ins = InputSequence(System.`in`)
        when (arg) {
            "-?" -> {
                println("usage: rpnc [options]")
                println("  Compile expression from stdin and emit instructions to stdout.")
                println("  -t  tokenize only")
                println("  -p  parse only")
                println("  -o  optimize")
            }
            "-t" -> {
                val tokens = Lexer.create(ins)
                tokens.forEach { println(it) }
            }
            "-p" -> {
                val ast = Parser.create(Lexer.create(ins))
                println(ast.format())
            }
            "-o" -> {
                println("TODO")
            }
            null -> {
                println("TODO")
            }
            else ->
                println("$arg: unrecognized option")
        }
    } catch (e: Exception) {
        println(e.message)
    }
}

private class InputSequence(ins: InputStream) : Sequence<Char> {
    private val reader = BufferedReader(InputStreamReader(ins, "UTF-8"))
    private val acc = StringBuilder()
    private var c: Int? = null

    override operator fun iterator() = object : Iterator<Char> {
        init {
            if (c == null) c = reader.read()
        }

        val iter = acc.iterator()

        override operator fun hasNext(): Boolean {
            return iter.hasNext() || c != -1
        }

        override operator fun next(): Char {
            return if (iter.hasNext())
                iter.next()
            else {
                acc.append(c!!.toChar())
                c = reader.read()
                iter.next()
            }
        }
    }
}
