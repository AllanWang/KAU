package kau

/**
 * Some common buildscript plugins, backed by the supplied versions
 */
object Plugins {
  const val android = "com.android.tools.build:gradle:${Versions.gradlePlugin}"
  const val aboutLibraries =
    "com.mikepenz.aboutlibraries.plugin:aboutlibraries-plugin:${Versions.aboutLibraries}"
  const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
  const val playPublisher =
    "com.github.triplet.gradle:play-publisher:${Versions.playPublishPlugin}"
  const val dexCount =
    "com.getkeepsafe.dexcount:dexcount-gradle-plugin:${Versions.dexCountPlugin}"
  const val gitVersion =
    "com.gladed.androidgitversion:gradle-android-git-version:${Versions.gitVersionPlugin}"
  const val spotless = "com.diffplug.spotless:spotless-plugin-gradle:${Versions.spotless}"
  const val hilt = "com.google.dagger:hilt-android-gradle-plugin:${Versions.hilt}"
}
