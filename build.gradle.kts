plugins {
    kotlin("jvm") version "1.3.41"
    id("org.jetbrains.dokka") version "0.9.18"
}

version = "0.1"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    testImplementation(kotlin("reflect"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.5.1")
}

tasks.test {
    useJUnitPlatform()
}

tasks.register("package") {
    dependsOn("packageZip")
    dependsOn("packageTar")
}

tasks.register<Zip>("packageZip") {
    dependsOn("packagePrepare")
    from("$buildDir/package")
}

tasks.register<Tar>("packageTar") {
    dependsOn("packagePrepare")
    from("$buildDir/package")
}

tasks.register("packagePrepare") {
    dependsOn("build")
    dependsOn("packageCopyScripts")
    dependsOn("packageCopyLibs")
}

tasks.register<Copy>("packageCopyScripts") {
    from("$rootDir/src/main/shell")
    into("$buildDir/package/rpn-$version")
}

tasks.register<Copy>("packageCopyLibs") {
    dependsOn(configurations.runtimeClasspath)
    from(file("$buildDir/libs"))
    from(configurations.runtimeClasspath)
    into("$buildDir/package/rpn-$version/lib")
}
