# RPN Compiler and Interpreter (Kotlin version)
A Kotlin implementation of the RPN compiler and interpreter. See the original [RPN project](https://github.com/davidledwards/rpn) for documentation.

## Comments
As mentioned above, this project was a direct port of the original implementation written in Scala. Even though the grammar of the RPN language is quite simple, the compiler itself is a nontrivial undertaking, which makes an ideal playground for learning a new functional programming language and exercising its core features.

A key and distinguishing feature present in Scala, but notably absent in Kotlin, is very robust and concise pattern matching. The absence of this feature was quite frustrating at first, because certain expressions in Scala required a different approach in Kotlin. The resulting expressions in Kotlin are more verbose and feel a bit clumsy, but there are practical tradeoffs that must be considered. The designers of Kotlin understood that replicating such robustness would result in far greater complexity in the compiler and slower compilation speeds, something that is noticeably present when using Scala.

## Building
This project is built using [Gradle](https://gradle.org/). After installing `gradle`, clone the repository and issue the command `gradle package` in the root directory of the project. This produces a redistributable assembly under the subdirectory `build/distributions` in the form of a single file, either `rpn-kotlin-<version>.tar.gz` or `rpn-kotlin-<version>.zip`.

Unpacking the assembly produces a directory structure with the following format:
```
rpn-<version>/
+ bin/
  + rpnc
  + rpn
+ lib/
  ...
```

For convenience, you might place `rpn-<version>/bin/rpnc` and `rpn-<version>/bin/rpn` in your `PATH` or create an alias.

# License
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
