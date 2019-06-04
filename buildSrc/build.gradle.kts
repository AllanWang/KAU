import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
    kotlin("jvm") version "1.3.31"
}

gradlePlugin {
    plugins {
        register("kau-plugin") {
            id = "ca.allanwang.kau"
            implementationClass = "ca.allanwang.kau.KauPlugin"
        }
    }
}

repositories {
    jcenter()
}
dependencies {
    implementation(kotlin("stdlib-jdk8"))
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}