package ca.allanwang.kau

/**
 * Some common buildscript plugins, backed by the supplied versions
 */
open class Plugins {
    private val v = Versions()
    val android = "com.android.tools.build:gradle:${v.gradlePlugin}"
    val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${v.kotlin}"
    val androidMaven = "com.github.dcendents:android-maven-gradle-plugin:${v.mavenPlugin}"
    val playPublisher = "com.github.triplet.gradle:play-publisher:${v.playPublishPlugin}"
    val dexCount = "com.getkeepsafe.dexcount:dexcount-gradle-plugin:${v.dexCountPlugin}"
    val gitVersion = "com.gladed.androidgitversion:gradle-android-git-version:${v.gitVersionPlugin}"
    val spotless = "com.diffplug.spotless:spotless-plugin-gradle:${v.spotless}"
}