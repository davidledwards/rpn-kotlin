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
        return codes
    }
}
