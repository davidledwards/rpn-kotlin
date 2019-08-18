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

import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.reflect.KClass

/**
 * An evaluator that computes the result of an instruction sequence.
 */
interface Evaluator : (Sequence<Code>) -> Double {
    companion object {
        fun create(resolver: (String) -> Double?): Evaluator = BasicEvaluator(resolver)
        fun create(codes: Sequence<Code>, resolver: (String) -> Double?): Double = create(resolver)(codes)
    }
}

private class BasicEvaluator(val resolver: (String) -> Double?) : Evaluator {
    override fun invoke(codes: Sequence<Code>): Double {
        fun evaluate(codes: Sequence<Code>, stack: List<Double>, syms: Map<String, Double>): Double {
            return when (val c = codes.firstOrNull()) {
                is DeclareSymbolCode -> {
                    resolver(c.name)?.let {
                        evaluate(codes.drop(1), stack, syms + Pair(c.name, it))
                    } ?: throw Exception("${c.name}: symbol not bound")
                }
                is PushSymbolCode -> {
                    syms.get(c.name)?.let {
                        evaluate(codes.drop(1), it + stack, syms)
                    } ?: throw Exception("${c.name}: symbol not bound")
                }
                is PushCode ->
                    evaluate(codes.drop(1), c.value + stack, syms)
                is OperatorCode -> {
                    val vs = stack.take(c.args)
                    if (vs.size == c.args) {
                        val op = let {
                            val _c: OperatorCode = c
                            operators.getValue(_c::class)
                        }
                        evaluate(codes.drop(1), vs.reversed().reduce(op) + stack.drop(c.args), syms)
                    } else
                        throw Exception("evaluator stack underflow")
                }
                null -> {
                    stack.firstOrNull() ?: throw Exception("evaluator stack size should be 1, but is ${stack.size}")
                }
                else ->
                    evaluate(codes.drop(1), stack, syms)
            }
        }
        return evaluate(codes, emptyList(), emptyMap())
    }
}

val operators: Map<KClass<out OperatorCode>, (Double, Double) -> Double> = mapOf(
    AddCode::class to { l, r -> l + r },
    SubtractCode::class to { l, r -> l - r },
    MultiplyCode::class to { l, r -> l * r },
    DivideCode::class to { l, r -> l / r },
    MinCode::class to { l, r -> min(l, r) },
    MaxCode::class to { l, r -> max(l, r) },
    ModuloCode::class to { l, r -> l % r },
    PowerCode::class to { base, exp -> base.pow(exp) }
)

private operator fun Double.plus(tail: List<Double>) = listOf(this) + tail
