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
 * An emitter that transforms a list of instructions into a sequence of strings.
 */
interface Emitter : (List<Code>) -> Sequence<String> {
    companion object {
        fun create(): Emitter = BasicEmitter()
        fun create(codes: List<Code>): Sequence<String> = create()(codes)
    }
}

private class BasicEmitter : Emitter {
    override fun invoke(codes: List<Code>): Sequence<String> {
        return codes.asSequence().map { it.repr }
    }
}
