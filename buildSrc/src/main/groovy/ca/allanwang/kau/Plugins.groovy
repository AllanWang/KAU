package ca.allanwang.kau

/**
 * Some common buildscript plugins, backed by the supplied versions
 */
class Plugins {
    static def android = "com.android.tools.build:gradle:${Versions.gradlePlugin}"
    static def kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    static
    def androidMaven = "com.github.dcendents:android-maven-gradle-plugin:${Versions.mavenPlugin}"
    static
    def playPublisher = "com.github.triplet.gradle:play-publisher:${Versions.playPublishPlugin}"
    static
    def dexCount = "com.getkeepsafe.dexcount:dexcount-gradle-plugin:${Versions.dexCountPlugin}"
    static
    def gitVersion = "gradle.plugin.com.gladed.gradle.androidgitversion:gradle-android-git-version:${Versions.gitVersionPlugin}"
}