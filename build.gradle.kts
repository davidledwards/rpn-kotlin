plugins {
    kotlin("jvm") version "1.3.41"
    id("org.jetbrains.dokka") version "0.9.18"
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    compile(kotlin("reflect"))
    implementation(kotlin("stdlib-jdk8"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.5.1")
}

tasks.test {
    useJUnitPlatform()
}