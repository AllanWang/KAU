package ca.allanwang.kau

/**
 * Some common dependencies, backed by the supplied versions
 */
class Dependencies {
    static def kotlin = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}"
    static def kotlinTest = "org.jetbrains.kotlin:kotlin-test-junit:${Versions.kotlin}"
    static def junit = "junit:junit:${Versions.junit}"
    static def espresso = "androidx.test.espresso:espresso-core:${Versions.espresso}"
    static def testRunner = "androidx.test.ext:junit:${Versions.testRunner}"
    static def testRules = "androidx.test:rules:${Versions.testRules}"
}