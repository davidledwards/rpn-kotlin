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
    Interpeter.main(args)
}

object Interpeter {
    fun main(args: Array<String>) = try {
        val ins = InputSequence(System.`in`)
        when (val arg = args.firstOrNull()) {
            "-?" -> {
                println("usage: rpn [option] [sym val]...")
                println("  Evaluate instructions from stdin and print result to stdout.")
                println("  Binds optional sequence of sym/val pairs prior to evaluation.")
                println("  -s  print symbols")
            }
            "-s" -> {
                val codes = Loader.create(ins)
                Codes.symbols(codes).forEach { println(it) }
            }
            else -> {
                if (arg == null || arg.take(1) != "-") {
                    fun bind(args: List<String>, syms: Map<String, Double>): Map<String, Double> {
                        val head = args.take(2)
                        return when (head.size) {
                            2 -> {
                                val s = head.get(0)
                                val v = head.get(1)
                                bind(args.drop(2), try {
                                    syms + (s to v.toDouble())
                                } catch (_: NumberFormatException) {
                                    println("$s <- $v: discarding symbol since value is malformed")
                                    syms
                                })
                            }
                            1 -> {
                                println("$arg: discarding symbol since value is missing")
                                syms
                            }
                            else -> syms
                        }
                    }
                    val syms = bind(args.toList(), emptyMap())
                    val result = Evaluator.create(Loader.create(ins)) { syms.get(it) }
                    println(result)
                } else {
                    println("$arg: unrecognized option")
                }
            }
        }
    } catch (e: Exception) {
        println(e.message)
    }
}
