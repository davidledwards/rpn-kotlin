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

import kotlin.reflect.KClass

/**
 * An optimizer that transforms a list of instructions into another list of
 * instructions.
 */
interface Optimizer : (List<Code>) -> List<Code> {
    companion object {
        fun create(): Optimizer = BasicOptimizer()
        fun create(codes: List<Code>): List<Code> = create()(codes)

        val disabled : Optimizer = object : Optimizer {
            override fun invoke(codes: List<Code>): List<Code> = codes
        }
    }
}

private class BasicOptimizer : Optimizer {
    override fun invoke(codes: List<Code>): List<Code> {
        tailrec fun optimize(codes: List<Code>): List<Code> {
            val _codes = transforms.fold(codes) { cs, fn -> fn(cs) }
            return if (_codes.size < codes.size) optimize(_codes) else _codes
        }
        return optimize(codes)
    }

    private val transforms: List<(List<Code>) -> List<Code>> = listOf(
        ::combineDynamicOperators,
        ::flattenDynamicOperators,
        ::evaluateLiteralExpressions
    )

    private val dynamicCtors: Map<KClass<out DynamicOperatorCode>, (Int) -> DynamicOperatorCode> = mapOf(
        AddCode::class to ::AddCode,
        SubtractCode::class to ::SubtractCode,
        MultiplyCode::class to ::MultiplyCode,
        DivideCode::class to ::DivideCode,
        MinCode::class to ::MinCode,
        MaxCode::class to ::MaxCode
    )

    private val operatorCtors: Map<KClass<out Code>, (Int) -> Code> = mapOf<KClass<out Code>, (Int) -> Code>(
        ModuloCode::class to { _ -> ModuloCode() },
        PowerCode::class to { _ -> PowerCode() }
    ) + dynamicCtors

    /**
     * An optimization that combines a series of identical operations as they appear in the
     * original source.
     * 
     * Consider the following input: `x + y + z`
     * 
     * The parser generates an AST that first evaluates `x + y`, then evaluates the result of
     * that expression and `z`. The corresponding bytecode follows:
     * {{{
     * push x
     * push y
     * add 2
     * push z
     * add 2
     * }}}
     * 
     * The `add 2` instruction tells the interpreter to pop `2` elements from the evaluation
     * stack, compute the sum, and push the result onto the stack. Since the `add` instruction
     * can operate on any number of arguments, both `add` operations can be combined into a
     * single instruction:
     * {{{
     * push x
     * push y
     * push z
     * add 3
     * }}}
     * 
     * A slightly more complicated example illustrates the same principle. Consider the input,
     * `a + (b * c) + d`, and the corresponding bytecode:
     * {{{
     * push a
     * push b
     * push c
     * mul 2
     * add 2
     * push d
     * add 2
     * }}}
     * 
     * Similar to the first scenario, both `add` operations can be combined even though the
     * intervening expression `b * c` exists. Note that adjacency of instructions is not
     * relevant, but rather the equivalence of the evaluation stack frame depth. In other
     * words, all operations of the same type at the same frame can be combined into a single
     * operation.
     * 
     * The algorithm works by simulating execution using an evaluation stack, maintaining a
     * set of instructions that are candidates for elimination. If another instruction at the
     * same frame depth is encountered, the original instruction is replaced with a `nop` and
     * the current instruction modified to evaluate additional elements on the stack. Once
     * all instructions have been evaluated, the set of revisions are applied, resulting in
     * a new sequence of instructions.
     */
    private fun combineDynamicOperators(codes: List<Code>): List<Code> {
        val (_, _, revs) = codes.fold(Triple(0, emptyList<Frame>(), emptyMap<Int, Code>())) {
            (pos, frames, revs), code ->
                val (_frames, _revs) = when (code) {
                    is PushSymbolCode, is PushCode -> {
                        // Such codes are not eligible for optimization.
                        Pair(IneligibleFrame() + frames, revs)
                    }
                    is DynamicOperatorCode -> {
                        // Consider only last argument on stack frame as potentially eligible, and only
                        // if operator names match. The intuition behind examining only the last argument,
                        // which is actually the first when viewed from a left-to-right evaluation
                        // standpoint, is that the prior eligible operator would have occurred at the
                        // same frame depth as the current operator.
                        val (_code, _revs) = let {
                            val frame = frames.take(code.args).lastOrNull()
                            when {
                                frame is EligibleFrame && frame.op == code.op -> {
                                    // Create modified instruction to reflect increase in number of arguments,
                                    // and turn eligible instruction into `nop` by including in revision set.
                                    val _code: DynamicOperatorCode = code
                                    val _c = dynamicCtors.getValue(_code::class)(_code.args + frame.code.args - 1)
                                    Pair(_c, revs + (frame.pos to NopCode()) + (pos to _c))
                                }
                                else ->
                                    Pair(code, revs)
                            }
                        }
                        Pair(EligibleFrame(_code.op, pos, _code) + frames.drop(code.args), _revs)
                    }
                    is OperatorCode -> {
                        // Operators with fixed number of operands not eligible for optimization.
                        Pair(IneligibleFrame() + frames.drop(code.args), revs)
                    }
                    else ->
                        Pair(frames, revs)
                }
            Triple(pos + 1, _frames, _revs)
        }
        return revise(codes, revs)
    }

    // These represent what to do with stack frames when combining dynamic operators.
    private interface Frame
    private class EligibleFrame(val op: String, val pos: Int, val code: DynamicOperatorCode) : Frame
    private class IneligibleFrame : Frame

    // A convenient prepend operation to a list of frames.
    private operator fun Frame.plus(frames: List<Frame>) = listOf(this) + frames

    /**
     * An optimization that flattens identical operations adjacent to each other in the
     * instruction sequence.
     * 
     * This optimization is similar to [[combineDynamicOperators]] in that operations are
     * essentially combined, but instead it looks for special cases in which identical operations
     * occur in adjacent frames on the evaluation stack.
     * 
     * Consider the input, `x * (y * z)`, and the corresponding bytecode:
     * {{{
     * push x
     * push y
     * push z
     * mul 2
     * mul 2
     * }}}
     * 
     * Note that both `mul` instructions occur in adjacent positions. At first glance, it may
     * appear as though [[combineDynamicOperators]] would eliminate one of the operations, but
     * each occurs at a different frame on the evaluation stack.
     * 
     * The intuition behind this optimization is that the first `mul 2` would push its result
     * onto the stack, only to be removed for evaluation by the second `mul 2` instruction. So,
     * rather than performing an intermediate calculation, the first can be eliminated in lieu of
     * a single `mul 3` instruction. In general, any number of adjacent identical instructions
     * can be reduced to a single instruction.
     * 
     * One may notice that this phase optimizes right-to-left evaluation scenarios, but only for
     * those operators with the associative property, i.e. evaluation can be left-to-right or
     * right-to-left. This becomes more clear with another example: `a * (b * (c * d))`.
     * The original instruction sequence follows:
     * {{{
     * push a
     * push b
     * push c
     * push d
     * mul 2
     * mul 2
     * mul 2
     * }}}
     * 
     * In essence, the parentheses are being removed and evaluated in a left-to-right manner
     * by eliminating all but the last `mul` instruction:
     * {{{
     * push a
     * push b
     * push c
     * push d
     * mul 4
     * }}}
     * 
     * The algorithm works by stepping through each associative operator instruction, finding
     * adjacent identical pairs, and eliminating all but the final instruction, which is then
     * modified to reflect the combined number of arguments.
     */
    private fun flattenDynamicOperators(codes: List<Code>): List<Code> {
        val initial: Triple<Int, DynamicOperatorCode?, Map<Int, Code>> = Triple(0, null, emptyMap())
        val (_, _, revs) = codes.fold(initial) {
            (pos, prior, revs), code ->
                val (p, r) = when {
                    code is DynamicOperatorCode && code.isAssociative -> when (prior?.op) {
                        code.op -> {
                            // Current instruction matches previous instruction, so do the following:
                            // - replace last instruction with `nop`, which may overwrite revision from
                            //   prior instruction evaluation
                            // - modify current instruction to reflect combined argument count
                            val _code: DynamicOperatorCode = code
                            val rep = dynamicCtors.getValue(_code::class)(_code.args + prior.args - 1)
                            Pair(rep, revs + ((pos - 1) to NopCode()) + (pos to rep))
                        }
                        else ->
                            Pair(code, revs)
                    }
                    else ->
                        Pair(null, revs)
                }
                Triple(pos + 1, p, r)
        }
        return revise(codes, revs)
    }

    /**
     * An optimization that evaluates literal expressions.
     * 
     * This optimization finds expressions containing only literal values and reduces them to a
     * single value, thereby eliminating the need for the interpreter to perform the computation.
     * 
     * Consider the input, `x + 1 + y + 2`, which produces the following sequence of
     * unoptimized instructions:
     * {{{
     * push x
     * push 1
     * add 2
     * push y
     * add 2
     * push 2
     * add 2
     * }}}
     * 
     * Applying the [[combineDynamicOperators]] optimization produces the following:
     * {{{
     * push x
     * push 1
     * push y
     * push 2
     * add 4
     * }}}
     * 
     * In either case, there is still opportunity to further optimize. Had the original input
     * been written as `x + y + 1 + 2`, it becomes more clear that `1 + 2` could be replaced
     * with `3`. The purpose of this optimization phase is to find such expressions and reduce
     * them to a single value.
     * 
     * In the latter optimized case above, applying this optimization reduces the instruction
     * sequence to the following:
     * {{{
     * push x
     * push y
     * push 3
     * add 3
     * }}}
     * 
     * The algorithm works by simulating execution of the instruction sequence using an evaluation
     * stack, though recording only literal values. As operations are encountered, the optimizer
     * peeks into the evaluation stack to determine if two or more literals are present, and if so,
     * eliminates the `push` instruction corresponding to each literal in lieu of a single `push`.
     * When an optimization is detected, the evaluation terminates and revisions are applied to
     * the original sequence of instructions. This process repeats itself until a complete
     * evaluation yields no new optimizations.
     * 
     * Note that an expression consisting entirely of literals will always be reduced to a single
     * `push` instruction containing the computed value.
     */
    private fun evaluateLiteralExpressions(codes: List<Code>): List<Code> {
        // TODO
        return codes
    }

    private fun revise(codes: List<Code>, revs: Map<Int, Code>): List<Code> {
        return if (revs.isEmpty())
            codes
        else {
            val (_, cs) = codes.fold(Pair(0, emptyList<Code>())) {
                (pos, revised), code ->
                    Pair(pos + 1, when (val c = revs.get(pos)) {
                        is NopCode -> revised
                        null -> revised + code
                        else -> revised + c
                    })
            }
            cs
        }
    }
}
