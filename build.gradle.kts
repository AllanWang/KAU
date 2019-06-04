import ca.allanwang.kau.ChangelogGenerator
import ca.allanwang.kau.KauPlugin

buildscript {

    repositories {
        google()
        jcenter()
        maven("https://maven.fabric.io/public")
        maven("https://plugins.gradle.org/m2/")
    }

    dependencies {
        // https://mvnrepository.com/artifact/com.android.tools.build/gradle?repo=google
        classpath("com.android.tools.build:gradle:3.4.1")
        // https://kotlinlang.org/docs/reference/using-gradle.html
        classpath(kotlin("gradle-plugin", version = "1.3.31"))
        // https://github.com/dcendents/android-maven-gradle-plugin/releases
        classpath("com.github.dcendents:android-maven-gradle-plugin:2.1")
        // https://github.com/Triple-T/gradle-play-publisher/releases
        classpath("com.github.triplet.gradle:play-publisher:2.1.0")
        // https://github.com/KeepSafe/dexcount-gradle-plugin/releases
        classpath("com.getkeepsafe.dexcount:dexcount-gradle-plugin:0.8.6")
        // https://github.com/gladed/gradle-android-git-version/releases
        classpath("com.gladed.androidgitversion:gradle-android-git-version:0.4.9")
        // https://github.com/diffplug/spotless/blob/master/plugin-gradle/CHANGES.md
        classpath("com.diffplug.spotless:spotless-plugin-gradle:3.21.1")
    }
}

apply<KauPlugin>()

task<Delete>("clean") {
    delete(rootProject.buildDir)
}

configure<ChangelogGenerator> {
    task<Exec>("generateChangelogMd") {
        generate("${project.rootDir}/sample/src/main/res/xml/kau_changelog.xml")
    }
}

subprojects {

    if (name == "gradle-plugin")
        return@subprojects

    apply(plugin = "com.gladed.androidgitversion")

    apply(from = "../spotless.gradle")

    repositories {
        google()
        jcenter()
        maven("https://jitpack.io")
    }
}