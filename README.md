# RPN Compiler and Interpreter (Kotlin version)
A Kotlin implementation of the RPN compiler and interpreter. See the original [RPN project](https://github.com/davidledwards/rpn) for documentation.

## Comments
TODO

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
