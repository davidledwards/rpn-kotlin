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
 * An instruction loader that transforms a sequence of characters into a sequence of
 * instructions.
 */
interface Loader : (Sequence<Char>) -> Sequence<Code>

fun Loader(): Loader = BasicLoader()
fun Loader(ins: Sequence<Char>): Sequence<Code> = Loader()(ins)

private class BasicLoader : Loader {
    override fun invoke(ins: Sequence<Char>): Sequence<Code> {
        fun load(ins: Sequence<Char>): Sequence<Code> {
            val (repr, rest) = read(ins)
            return if (repr == null)
                emptySequence<Code>()
            else {
                Codes.parse(repr)?.let { sequenceOf(it) + load(rest) }
                    ?: throw Exception("$repr: unrecognized or malformed instruction")
            }
        }
        return load(ins)
    }

    private fun read(ins: Sequence<Char>): Pair<String?, Sequence<Char>> {
        tailrec fun slurp(ins: Sequence<Char>, repr: String): Pair<String?, Sequence<Char>> {
            return when (val c = ins.firstOrNull()) {
                '\n' -> Pair(repr, ins.drop(1))
                null -> Pair(if (repr.length == 0) null else repr, ins)
                else -> slurp(ins.drop(1), repr + c)
            }
        }
        return slurp(ins, "")
    }
}
