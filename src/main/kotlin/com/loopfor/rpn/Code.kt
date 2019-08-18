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
 * Represents a bytecode instruction generated from a syntax tree.
 * 
 * Recognized instructions:
 * ```
 * sym <symbol>
 * pushsym <symbol>
 * push <number>
 * add <args>
 * sub <args>
 * mul <args>
 * div <args>
 * min <args>
 * max <args>
 * mod
 * pow
 * nop
 * ```
 */
sealed class Code(val op: String) {
    abstract val repr: String
}

abstract class BasicCode(op: String) : Code(op) {
    override val repr = op
}

abstract class OperatorCode(op: String, val args: Int) : Code(op) {
    abstract val isAssociative: Boolean
    abstract val isCommutative: Boolean
}

abstract class FixedOperatorCode(op: String, args: Int) : OperatorCode(op, args) {
    override val repr = op
}

abstract class DynamicOperatorCode(op: String, args: Int) : OperatorCode(op, args) {
    override val repr = "$op $args"
}

/**
 * Declares a symbol before its use, which allows the interpreter to bind a value.
 */
class DeclareSymbolCode(val name: String) : Code("sym") {
    override val repr = "$op $name"

    companion object {
        private val pattern = """\s*sym\s+([a-zA-Z]+)\s*""".toRegex()

        fun match(repr: String): DeclareSymbolCode? =
            pattern.matchEntire(repr)?.let {
                val (name) = it.destructured
                DeclareSymbolCode(name)
            }
    }
}

/**
 * Pushes the symbol `name` onto the evaluation stack.
 */
class PushSymbolCode(val name: String) : Code("pushsym") {
    override val repr = "$op $name"

    companion object {
        private val pattern = """\s*pushsym\s+([a-zA-Z]+)\s*""".toRegex()

        fun match(repr: String): PushSymbolCode? =
            pattern.matchEntire(repr)?.let {
                val (name) = it.destructured
                PushSymbolCode(name)
            }
    }
}

/**
 * Pushes the number `value` onto the evaluation stack.
 */
class PushCode(val value: Double) : Code("push") {
    private val canonical =
        String.format("%.10f", value).reversed().dropWhile { it == '0' }.dropWhile { it == '.' }.reversed()

    override val repr = "$op $canonical"

    companion object {
        private val pattern = """\s*push\s+(-?\d+|-?\d+\.\d+)\s*""".toRegex()

        fun match(repr: String): PushCode? =
            pattern.matchEntire(repr)?.let {
                val (value) = it.destructured
                try {
                    PushCode(value.toDouble())
                } catch (_: NumberFormatException) {
                    null
                }
            }
    }
}

/**
 * Pops `args` operands from the evaluation stack, computes the sum, and pushes the
 * result onto the evaluation stack.
 */
class AddCode(args: Int) : DynamicOperatorCode("add", args) {
    override val isAssociative = true
    override val isCommutative = true

    companion object {
        private val pattern = """\s*add\s+(\d+)\s*""".toRegex()

        fun match(repr: String): AddCode? =
            pattern.matchEntire(repr)?.let {
                val (args) = it.destructured
                verify(args, 2) { AddCode(it) }
            }
    }
}

/**
 * Pops `args` operands from the evaluation stack, computes the difference, and pushes
 * the result onto the evaluation stack.
 */
class SubtractCode(args: Int) : DynamicOperatorCode("sub", args) {
    override val isAssociative = false
    override val isCommutative = false

    companion object {
        private val pattern = """\s*sub\s+(\d+)\s*""".toRegex()

        fun match(repr: String): SubtractCode? =
            pattern.matchEntire(repr)?.let {
                val (args) = it.destructured
                verify(args, 2) { SubtractCode(it) }
            }
    }
}

/**
 * Pops `args` operands from the evaluation stack, computes the product, and pushes
 * the result onto the evaluation stack.
 */
class MultiplyCode(args: Int) : DynamicOperatorCode("mul", args) {
    override val isAssociative = true
    override val isCommutative = true

    companion object {
        private val pattern = """\s*mul\s+(\d+)\s*""".toRegex()

        fun match(repr: String): MultiplyCode? =
            pattern.matchEntire(repr)?.let {
                val (args) = it.destructured
                verify(args, 2) { MultiplyCode(it) }
            }
    }
}

/**
 * Pops `args` operands from the evaluation stack, computes the quotient, and pushes
 * the result onto the evaluation stack.
 */
class DivideCode(args: Int) : DynamicOperatorCode("div", args) {
    override val isAssociative = false
    override val isCommutative = false

    companion object {
        private val pattern = """\s*div\s+(\d+)\s*""".toRegex()

        fun match(repr: String): DivideCode? =
            pattern.matchEntire(repr)?.let {
                val (args) = it.destructured
                verify(args, 2) { DivideCode(it) }
            }
    }
}

/**
 * Pops `args` operands from the evaluation stack, computes the minimum, and pushes
 * the result onto the evaluation stack.
 */
class MinCode(args: Int) : DynamicOperatorCode("min", args) {
    override val isAssociative = true
    override val isCommutative = true

    companion object {
        private val pattern = """\s*min\s+(\d+)\s*""".toRegex()

        fun match(repr: String): MinCode? =
            pattern.matchEntire(repr)?.let {
                val (args) = it.destructured
                verify(args, 2) { MinCode(it) }
            }
    }
}

/**
 * Pops `args` operands from the evaluation stack, computes the maximum, and pushes
 * the result onto the evaluation stack.
 */
class MaxCode(args: Int) : DynamicOperatorCode("max", args) {
    override val isAssociative = true
    override val isCommutative = true

    companion object {
        private val pattern = """\s*max\s+(\d+)\s*""".toRegex()

        fun match(repr: String): MaxCode? =
            pattern.matchEntire(repr)?.let {
                val (args) = it.destructured
                verify(args, 2) { MaxCode(it) }
            }
    }
}

/**
 * Pops two operands from the evaluation stack, computes the remainder, and pushes
 * the result onto the evaluation stack.
 */
class ModuloCode : FixedOperatorCode("mod", 2) {
    override val isAssociative = false
    override val isCommutative = false

    companion object {
        private val pattern = """\s*mod\s*""".toRegex()

        fun match(repr: String): ModuloCode? =
            pattern.matchEntire(repr)?.let { ModuloCode() }
    }
}

/**
 * Pops two operands from the evaluation stack, computes the exponentiation, and pushes
 * the result onto the evaluation stack.
 */
class PowerCode : FixedOperatorCode("pow", 2) {
    override val isAssociative = false
    override val isCommutative = false

    companion object {
        private val pattern = """\s*pow\s*""".toRegex()

        fun match(repr: String): PowerCode? =
            pattern.matchEntire(repr)?.let { PowerCode() }
    }
}

/**
 * An instruction that has no effect.
 */
class NopCode : BasicCode("nop") {
    companion object {
        private val pattern = """\s*nop\s*""".toRegex()

        fun match(repr: String): NopCode? =
            pattern.matchEntire(repr)?.let { NopCode() }
    }
}

fun List<Code>.format(): String {
    val (_, _, outs) = this.fold(Triple(0, 0, "")) {
        (pos, frame, outs), c ->
            val f = when (c) {
                is PushSymbolCode -> 1
                is PushCode -> 1
                is OperatorCode -> 1 - c.args
                else -> 0
            }
            Triple(pos + 1, frame + f, outs + "$pos [$frame] ${c.repr}\n")
    }
    return outs
}

private fun <T : Code> verify(args: String, lower: Int, fn: (Int) -> T): T? {
    try {
        val n = args.toInt()
        return if (n < lower) null else fn(n)
    } catch (_: NumberFormatException) {
        return null
    }
}

object Codes {
    fun symbols(codes: Sequence<Code>): Sequence<String> {
        return codes.flatMap { c ->
            if (c is DeclareSymbolCode) sequenceOf(c.name) else emptySequence()
        }
    }

    fun parse(repr: String): Code? {
        return DeclareSymbolCode.match(repr)
            ?: PushSymbolCode.match(repr)
            ?: PushCode.match(repr)
            ?: AddCode.match(repr)
            ?: SubtractCode.match(repr)
            ?: MultiplyCode.match(repr)
            ?: DivideCode.match(repr)
            ?: MinCode.match(repr)
            ?: MaxCode.match(repr)
            ?: ModuloCode.match(repr)
            ?: PowerCode.match(repr)
            ?: NopCode.match(repr)
    }
}
