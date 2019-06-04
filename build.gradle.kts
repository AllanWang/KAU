buildscript {
    repositories {
        google()
        jcenter()
        maven("https://maven.fabric.io/public")
        maven ( "https://plugins.gradle.org/m2/")
    }

    apply(plugin="ca.allanwang.kau")

    dependencies {
        classpath(kauPlugin.android)
        classpath(kauPlugin.kotlin)
        classpath(kauPlugin.androidMaven)
        classpath(kauPlugin.playPublisher)
        classpath(kauPlugin.dexCount)
        classpath(kauPlugin.gitVersion)
        classpath(kauPlugin.spotless)
    }

    wrapper.setDistributionType(Wrapper.DistributionType.ALL)
}

apply<ca.allanwang.kau.KauPlugin>()

apply(plugin="ca.allanwang.kau")

task<Delete>("clean") {
    delete(rootProject.buildDir)
}

task<Exec>("generateChangelogMd") {
    kauChangelog.generate("$project.rootDir/sample/src/main/res/xml/kau_changelog.xml")
}

subprojects {

    if (name == "gradle-plugin")
        return@subprojects

    apply(plugin="com.gladed.androidgitversion")

    apply(from="../spotless.gradle")

    repositories {
        google()
        jcenter()
        maven("https://jitpack.io")
    }
}
