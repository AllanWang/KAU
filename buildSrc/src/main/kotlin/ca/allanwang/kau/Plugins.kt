package ca.allanwang.kau

/**
 * Some common buildscript plugins, backed by the supplied versions
 */
open class Plugins {
    companion object {
        const val android = "com.android.tools.build:gradle:${Versions.gradlePlugin}"
        const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
        const val androidMaven = "com.github.dcendents:android-maven-gradle-plugin:${Versions.mavenPlugin}"
        const val playPublisher = "com.github.triplet.gradle:play-publisher:${Versions.playPublishPlugin}"
        const val dexCount = "com.getkeepsafe.dexcount:dexcount-gradle-plugin:${Versions.dexCountPlugin}"
        const val gitVersion =
                "gradle.plugin.com.gladed.gradle.androidgitversion:gradle-android-git-version:${Versions.gitVersionPlugin}"
        const val spotless = "com.diffplug.spotless:spotless-plugin-gradle:${Versions.spotless}"
    }
}