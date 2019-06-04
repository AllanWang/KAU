plugins {
    `kotlin-dsl`
}

group = "ca.allanwang"

gradlePlugin {
    plugins {
        register("kau-plugin") {
            id = "ca.allanwang.kau"
            implementationClass = "ca.allanwang.kau.KauPlugin"
        }
    }
}

repositories {
    mavenCentral()
}