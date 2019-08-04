package com.loopfor.rpn

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader

class InputSequence(ins: InputStream) : Sequence<Char> {
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
