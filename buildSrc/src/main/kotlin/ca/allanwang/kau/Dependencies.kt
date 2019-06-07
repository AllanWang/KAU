package ca.allanwang.kau

/**
 * Some common dependencies, backed by the supplied versions
 */
open class Dependencies {
    private val v = Versions()
    val kotlin = "org.jetbrains.kotlin:kotlin-stdlib:${v.kotlin}"
    val kotlinTest = "org.jetbrains.kotlin:kotlin-test-junit:${v.kotlin}"
    val junit = "junit:junit:${v.junit}"
    val espresso = "androidx.test.espresso:espresso-core:${v.espresso}"
    val testRunner = "androidx.test.ext:junit:${v.testRunner}"
    val testRules = "androidx.test:rules:${v.testRules}"
}