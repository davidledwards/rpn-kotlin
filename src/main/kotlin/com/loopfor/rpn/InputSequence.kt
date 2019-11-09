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

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader

/**
 * Makes an input stream appear as a sequence of characters.
 */
class InputSequence(ins: InputStream) : Sequence<Char> {
    private val reader = BufferedReader(InputStreamReader(ins, "UTF-8"))
    private val acc = StringBuilder()
    private var c: Int? = null

    override operator fun iterator() = object : Iterator<Char> {
        init {
            // Reading the first character must wait until the iterator is created, otherwise creation of
            // the sequence may cause the program to prematurely block depending on the type of input stream.
            if (c == null) c = reader.read()
        }

        // Iteration actually happens against the buffer due to odd behavior of sequences as implemented
        // by the standard library. Note that use of a buffer defeats the purpose of using a sequence.
        val iter = acc.iterator()

        override operator fun hasNext(): Boolean {
            // Look at the string buffer iterator first, then when exhausted, look at the next character
            // read from the input stream.
            return iter.hasNext() || c != -1
        }

        override operator fun next(): Char {
            // Return next character in buffer iterator if one exists, otherwise append next character from
            // input stream to buffer.
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
