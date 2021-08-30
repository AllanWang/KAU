package kau

import org.gradle.api.JavaVersion

object Versions {
    const val coreMinSdk = 19
    const val minSdk = 21
    const val targetSdk = 30

    val java = JavaVersion.VERSION_11

    // https://mvnrepository.com/artifact/androidx.appcompat/appcompat?repo=google
    const val appcompat = "1.3.0-rc01"

    // https://mvnrepository.com/artifact/com.google.android.material/material
    const val googleMaterial = "1.3.0"

    // https://mvnrepository.com/artifact/androidx.recyclerview/recyclerview
    const val recyclerView = "1.2.0"

    // https://mvnrepository.com/artifact/androidx.swiperefreshlayout/swiperefreshlayout
    const val swipeRefreshLayout = "1.1.0"

    // https://mvnrepository.com/artifact/androidx.cardview/cardview
    const val cardView = "1.0.0"

    // https://mvnrepository.com/artifact/androidx.constraintlayout/constraintlayout
    const val constraintLayout = "2.1.0"

    // https://mvnrepository.com/artifact/androidx.core/core-ktx
    const val coreKtx = "1.6.0"

    // https://kotlinlang.org/docs/reference/using-gradle.html
    const val kotlin = "1.5.30"

    // https://github.com/Kotlin/kotlinx.coroutines/releases
    const val coroutines = "1.5.0"

    // https://github.com/mikepenz/AboutLibraries/releases
    const val aboutLibraries = "8.9.0"

    // Keep old version
    // https://github.com/wasabeef/Blurry/releases
    const val blurry = "4.0.0"

    // https://github.com/mikepenz/FastAdapter/releases
    const val fastAdapter = "5.4.1"

    // https://github.com/bumptech/glide/releases
    const val glide = "4.12.0"

    // https://github.com/mikepenz/Android-Iconics#1-provide-the-gradle-dependency
    const val iconics = "5.3.0-b01"
    const val iconicsGoogle = "4.0.0.1"
    const val iconicsMaterial = "2.2.0.8"
    const val iconicsCommunity = "5.8.55.0"

    // https://github.com/afollestad/material-dialogs/releases
    const val materialDialog = "3.3.0"

    // https://github.com/InsertKoinIO/koin/blob/master/CHANGELOG.md
    const val koin = "3.1.0"

    // https://github.com/google/dagger/releases
    // https://mvnrepository.com/artifact/com.google.dagger/hilt-android
    const val hilt = "2.37"

    // https://mvnrepository.com/artifact/androidx.ui/ui-core?repo=google
    const val compose = "0.1.0-dev14"

    // https://square.github.io/leakcanary/changelog/
    const val leakCanary = "2.7"

    // https://mvnrepository.com/artifact/androidx.test.espresso/espresso-core?repo=google
    const val espresso = "3.3.0"

    // https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-api
    const val junit = "4.13"

    // https://mvnrepository.com/artifact/androidx.test.ext/junit
    const val testRunner = "1.1.2"

    // https://mvnrepository.com/artifact/androidx.test/rules?repo=google
    const val testRules = "1.3.0"

    // https://github.com/diffplug/spotless/blob/master/plugin-gradle/CHANGES.md
    const val spotless = "5.7.0"

    // https://github.com/bugsnag/bugsnag-android/releases
    const val bugsnag = "5.9.0"

    // https://github.com/bugsnag/bugsnag-android-gradle-plugin/releases
    const val bugsnagPlugin = "5.7.6"

    // https://mvnrepository.com/artifact/com.android.tools.build/gradle?repo=google
    const val gradlePlugin = "7.1.0-alpha02"

    // https://github.com/Triple-T/gradle-play-publisher/releases
    const val playPublishPlugin = "3.4.0-agp7.0"

    // https://github.com/KeepSafe/dexcount-gradle-plugin/releases
    const val dexCountPlugin = "2.1.0-RC01"

    // https://github.com/gladed/gradle-android-git-version/releases
    const val gitVersionPlugin = "0.4.14"
}
