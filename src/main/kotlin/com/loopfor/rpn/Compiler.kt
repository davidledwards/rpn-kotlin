package com.loopfor.rpn

import java.io.InputStreamReader

fun main(args: Array<String>) {
    Compiler.main(args)
}

object Compiler {
    fun main(args: Array<String>) = try {
        val stdin = InputStreamReader(System.`in`, "UTF-8")
        val ins = generateSequence { 
            val c = stdin.read()
            println("--> $c")
            if (c == -1) null else c.toChar()
        }
        val tokens = Lexer.create(ins)
        println("begin")
        tokens.forEach { println(it) }
        println("end")
    } catch (e: Exception) {
        println(e.message)
    }
}