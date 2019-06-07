/**
 * Some common dependencies, backed by the supplied versions
 */
object Dependencies {
    const val kotlin = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}"
    const val kotlinTest = "org.jetbrains.kotlin:kotlin-test-junit:${Versions.kotlin}"
    const val junit = "junit:junit:${Versions.junit}"
    const val espresso = "androidx.test.espresso:espresso-core:${Versions.espresso}"
    const val testRunner = "androidx.test.ext:junit:${Versions.testRunner}"
    const val testRules = "androidx.test:rules:${Versions.testRules}"
}