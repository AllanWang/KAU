package ca.allanwang.kau

class Versions {
    static def coreMinSdk = 19
    static def minSdk = 21
    static def targetSdk = 28

    // https://developer.android.com/studio/releases/build-tools
    static def buildTools = '28.0.3'

    // https://developer.android.com/topic/libraries/support-library/revisions
    static def supportLibs = '28.0.0'

    // https://kotlinlang.org/docs/reference/using-gradle.html
    static def kotlin = '1.2.71'

    // https://github.com/mikepenz/AboutLibraries/releases
    static def aboutLibraries = '6.1.1'

    // https://github.com/Kotlin/anko/releases
    static def anko = '0.10.5'

    // https://github.com/wasabeef/Blurry/releases
    static def blurry = '2.1.1'

    // https://dl.google.com/dl/android/maven2/com/android/support/constraint/group-index.xml
    static def constraintLayout = '1.1.3'

    // https://github.com/mikepenz/FastAdapter#using-maven
    static def fastAdapter = '3.2.9'
    static def fastAdapterCommons = fastAdapter

    // https://github.com/bumptech/glide/releases
    static def glide = '4.8.0'

    // https://github.com/mikepenz/Android-Iconics#1-provide-the-gradle-dependency
    static def iconics = '3.0.4'
    static def iconicsGoogle = '3.0.1.2'
    static def iconicsMaterial = '2.2.0.4'
    static def iconicsCommunity = '2.0.46.1'

    // https://github.com/afollestad/material-dialogs/releases
    static def materialDialog = '0.9.6.0'

    static def espresso = '3.0.1'
    static def junit = '4.12'
    static def testRunner = '1.0.1'

    static def gradlePlugin = '3.2.1'
    static def mavenPlugin = '2.1'
    static def playPublishPlugin = '1.2.2'

    // https://github.com/KeepSafe/dexcount-gradle-plugin/releases
    static def dexCountPlugin = '0.8.3'
    static def gitVersionPlugin = '0.4.4'
}