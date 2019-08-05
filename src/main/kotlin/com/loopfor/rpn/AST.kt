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
 * Represents a syntax tree constructed by parsing a sequence of tokens.
 */
sealed class AST

data class SymbolAST(val name: String): AST()
data class NumberAST(val value: Double): AST()
data class AddAST(val l: AST, val r: AST): AST()
data class SubtractAST(val l: AST, val r: AST): AST()
data class MultiplyAST(val l: AST, val r: AST): AST()
data class DivideAST(val l: AST, val r: AST): AST()
data class ModuloAST(val l: AST, val r: AST): AST()
data class PowerAST(val base: AST, val exp: AST): AST()
data class MinAST(val l: AST, val r: AST): AST()
data class MaxAST(val l: AST, val r: AST): AST()

fun AST.format(): String {
    fun format(ast: AST, depth: Int): String {
        val text = when (ast) {
            is SymbolAST -> "Symbol(${ast.name})\n"
            is NumberAST -> "Number(${ast.value})\n"
            is AddAST -> "Add\n" + format(ast.l, depth + 1) + format(ast.r, depth + 1)
            is SubtractAST -> "Subtract\n" + format(ast.l, depth + 1) + format(ast.r, depth + 1)
            is MultiplyAST -> "Multiply\n" + format(ast.l, depth + 1) + format(ast.r, depth + 1)
            is DivideAST -> "Divide\n" + format(ast.l, depth + 1) + format(ast.r, depth + 1)
            is ModuloAST -> "Modulo\n" + format(ast.l, depth + 1) + format(ast.r, depth + 1)
            is PowerAST -> "Power\n" + format(ast.base, depth + 1) + format(ast.exp, depth + 1)
            is MinAST -> "Min\n" + format(ast.l, depth + 1) + format(ast.r, depth + 1)
            is MaxAST -> "Max\n" + format(ast.l, depth + 1) + format(ast.r, depth + 1)
        }
        return " ".repeat(depth) + text
    }
    return format(this, 0)
}
