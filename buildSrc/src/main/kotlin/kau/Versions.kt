package kau

import org.gradle.api.JavaVersion

object Versions {
  const val coreMinSdk = 19
  const val minSdk = 21
  const val targetSdk = 33

  val java = JavaVersion.VERSION_1_8

  // https://mvnrepository.com/artifact/androidx.appcompat/appcompat?repo=google
  const val appcompat = "1.5.0"

  // https://mvnrepository.com/artifact/com.google.android.material/material
  const val googleMaterial = "1.6.1"

  // https://mvnrepository.com/artifact/androidx.recyclerview/recyclerview
  const val recyclerView = "1.2.1"

  // https://mvnrepository.com/artifact/androidx.swiperefreshlayout/swiperefreshlayout
  const val swipeRefreshLayout = "1.1.0"

  // https://mvnrepository.com/artifact/androidx.cardview/cardview
  const val cardView = "1.0.0"

  // https://mvnrepository.com/artifact/androidx.constraintlayout/constraintlayout
  const val constraintLayout = "2.1.4"

  // https://mvnrepository.com/artifact/androidx.core/core-ktx
  const val coreKtx = "1.8.0"

  // https://kotlinlang.org/docs/reference/using-gradle.html
  const val kotlin = "1.7.10"

  // https://github.com/Kotlin/kotlinx.coroutines/releases
  const val coroutines = "1.6.4"

  // https://github.com/mikepenz/AboutLibraries/releases
  const val aboutLibraries = "10.4.0"

  // Keep old version
  // https://github.com/wasabeef/Blurry/tags
  const val blurry = "4.0.0"

  // https://github.com/mikepenz/FastAdapter/releases
  const val fastAdapter = "5.6.0"

  // https://github.com/bumptech/glide/releases
  const val glide = "4.13.2"

  // https://github.com/mikepenz/Android-Iconics#1-provide-the-gradle-dependency
  const val iconics = "5.3.4"
  const val iconicsGoogle = "4.0.0.2"
  const val iconicsMaterial = "2.2.0.8"
  const val iconicsCommunity = "6.4.95.0"

  // https://github.com/afollestad/material-dialogs/releases
  const val materialDialog = "3.3.0"

  // https://github.com/google/dagger/releases
  // https://mvnrepository.com/artifact/com.google.dagger/hilt-android
  const val hilt = "2.43.2"

  // https://mvnrepository.com/artifact/androidx.ui/ui-core?repo=google
  const val compose = "0.1.0-dev14"

  // https://square.github.io/leakcanary/changelog/
  const val leakCanary = "2.7"

  // https://mvnrepository.com/artifact/androidx.test.espresso/espresso-core?repo=google
  const val espresso = "3.4.0"

  // https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-api
  const val junit = "4.13"

  // https://mvnrepository.com/artifact/androidx.test.ext/junit
  const val testRunner = "1.1.2"

  // https://mvnrepository.com/artifact/androidx.test/rules?repo=google
  const val testRules = "1.4.0"

  // https://github.com/diffplug/spotless/blob/master/plugin-gradle/CHANGES.md
  const val spotless = "6.11.0"

  // https://github.com/facebookincubator/ktfmt/releases
  const val ktfmt = "0.40"

  // https://mvnrepository.com/artifact/com.android.tools.build/gradle?repo=google
  const val gradlePlugin = "7.3.0-rc01"

  // https://github.com/Triple-T/gradle-play-publisher/releases
  const val playPublishPlugin = "3.7.0"

  // https://github.com/KeepSafe/dexcount-gradle-plugin/releases
  const val dexCountPlugin = "3.1.0"

  // https://github.com/gladed/gradle-android-git-version/releases
  const val gitVersionPlugin = "0.4.14"
}
