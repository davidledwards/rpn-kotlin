# RPN Compiler and Interpreter (Kotlin version)

A Kotlin implementation of the RPN compiler and interpreter. See the original [RPN project](https://github.com/davidledwards/rpn) for documentation.

## Comments

As mentioned above, this project was a direct port of the original implementation written in Scala. Even though the grammar of the RPN language is quite simple, the compiler itself is a nontrivial undertaking, which makes an ideal playground for learning a new functional programming language and exercising its core features.

A key and distinguishing feature present in Scala, but notably absent in Kotlin, is very robust and concise pattern matching. The absence of this feature was quite frustrating at first, because certain expressions in Scala required a different approach in Kotlin. The resulting expressions in Kotlin are more verbose and feel a bit clumsy, but there are practical tradeoffs that must be considered. The designers of Kotlin understood that replicating such robustness would result in far greater complexity in the compiler and slower compilation speeds, something that is noticeably present when using Scala.

Perhaps one of the more challenging issues moving from Scala to Kotlin was the inconsistent behavior of `Sequence`. Scala provides a `Stream` interface as part of its standard library, which represents a lazy, and possibly infinite, sequence. Kotlin provides something similar through the `Sequence` interface, although I came to realize that the behavior was not consistent with expectations. I would argue that it violates the [principle of least surprise](https://en.wikipedia.org/wiki/Principle_of_least_astonishment), but of course, am happy to entertain opposing views.

In particular, implementations of `Sequence` may constrain themselves to be iterated only once. The unfortunate side effect is that one cannot reason absolutely about the behavior of `Sequence`.

To illustrate, the following throws an exception when the underlying `Sequence` is constrained to a single iteration.

```kotlin
val s = generateSequence { 1 }
s.first() // yields 1
s.first() // IllegalStateException
```

In contrast, the following example allows multiple iterations and behaves as one (influenced by Scala) would expect.

```kotlin
val s = listOf(1).asSequence()
s.first() // yields 1
s.first() // yields 1
```

Scala avoids this split brain problem by retaining state for any given reference to `Stream`, thus allowing multiple iterations, but solves the apparent problem of accumulating state by disposing of said state when references to earlier `Stream` views are garbage collected. This would allow one to iterate over sequences in a consistent manner, as demonstrated below.

```kotlin
val s: Sequence<T> = ...
s.first() // yields first T and accumulates in state
s.first() // yields first T from state

val t = s.drop(1) // returns new sequence, 's' goes away
t.first() // yields second T and accumulates in state
t.first() // yields second T from state
```

I suggest the authors of Kotlin take a page from Scala designers and rethink the behavior of `Sequence`.

## Building

This project is built using [Gradle](https://gradle.org/). After installing `gradle`, clone the repository and issue the command `gradle package` in the root directory of the project. This produces a redistributable assembly under the subdirectory `build/distributions` in the form of a single file, either `rpn-kotlin-<version>.tar.gz` or `rpn-kotlin-<version>.zip`.

Unpacking the assembly produces a directory structure with the following format:

```shell
rpn-<version>/
+ bin/
  + rpnc
  + rpn
+ lib/
  ...
```

For convenience, you might place `rpn-<version>/bin/rpnc` and `rpn-<version>/bin/rpn` in your `PATH` or create an alias.

## License

Copyright 2019 David Edwards

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
