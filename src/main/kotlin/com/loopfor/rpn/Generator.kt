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
 * A code generator that transforms a syntax tree to a list of instructions.
 * 
 * The instruction sequence should be considered unoptimized.
 */
interface Generator : (AST) -> List<Code>

fun Generator(): Generator = BasicGenerator()
fun Generator(ast: AST): List<Code> = Generator()(ast)

// Represents list of instruction codes and symbol names.
private typealias State = Pair<List<Code>, Set<String>>

private class BasicGenerator : Generator {
    override fun invoke(ast: AST): List<Code> = generate(ast)

    private fun generate(ast: AST): List<Code> {
        fun generate(ast: AST, state: State): State = when (ast) {
            is SymbolAST -> {
                val (codes, syms) = state
                Pair(codes + PushSymbolCode(ast.name), syms + ast.name)
            }
            is NumberAST -> {
                val (codes, syms) = state
                Pair(codes + PushCode(ast.value), syms)
            }
            is AddAST -> {
                val (codes, syms) = generate(ast.r, generate(ast.l, state))
                Pair(codes + AddCode(2), syms)
            }
            is SubtractAST -> {
                val (codes, syms) = generate(ast.r, generate(ast.l, state))
                Pair(codes + SubtractCode(2), syms)
            }
            is MultiplyAST -> {
                val (codes, syms) = generate(ast.r, generate(ast.l, state))
                Pair(codes + MultiplyCode(2), syms)
            }
            is DivideAST -> {
                val (codes, syms) = generate(ast.r, generate(ast.l, state))
                Pair(codes + DivideCode(2), syms)
            }
            is MinAST -> {
                val (codes, syms) = generate(ast.r, generate(ast.l, state))
                Pair(codes + MinCode(2), syms)
            }
            is MaxAST -> {
                val (codes, syms) = generate(ast.r, generate(ast.l, state))
                Pair(codes + MaxCode(2), syms)
            }
            is ModuloAST -> {
                val (codes, syms) = generate(ast.r, generate(ast.l, state))
                Pair(codes + ModuloCode(), syms)
            }
            is PowerAST -> {
                val (codes, syms) = generate(ast.exp, generate(ast.base, state))
                Pair(codes + PowerCode(), syms)
            }
        }

        // Instruction codes are generated and set of referenced symbols is gathered during traversal.
        val (codes, syms) = generate(ast, Pair(emptyList(), emptySet()))

        // Map set of symbol names to declarations and prepend to instruction codes.
        val decls = syms.toList().sorted().map { DeclareSymbolCode(it) }
        return decls + codes
    }
}
