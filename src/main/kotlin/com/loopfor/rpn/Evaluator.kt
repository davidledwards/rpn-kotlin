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
        return 0.0
    }
}