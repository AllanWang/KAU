# KAU :gradle-plugin

> Plugin helper for KAU

KAU holds quite a big collection of projects, with the intent on making it easy to reuse complex features, and also to update.
This plugin aims to help maintain version updates straight from the source, and also adds on a few nice functions.

As a note, this is located under `buildSrc` as it is automatically included when building 
Everything here is used when generating the library, so it's always tested.

## Contents
* [Usage](#usage)
* [Constants](#constants)
* [Changelog Generator](#changelog-generator)

## Usage

The easiest way to use this is to create your own `buildSrc` folder, and include `ca.allanwang.kau:gradle-plugin:${KAU}` as a dependency.
This way, you can also use items in your buildscript.
You can also add your own classes to manage versions in your app.

When making your own `buildSrc`, make sure you add the jitpack repository:

```gradle
repositories {
    ...
    maven { url "https://jitpack.io" }
}
```

---

Currently, the plugin is a collection of simple classes.

## Constants

`Versions`, `Plugins`, and `Dependencies` supply constants you can use for your classpath and dependencies.
Some dependencies are also exposed as functions

Eg

```gradle
dependencies {
    ...
    // The three dependencies below are all the same thing
    implementation "org.jetbrains.kotlin:kotlin-test-junit:${kau.Versions.kotlin}"
    implementation kau.Dependencies.kotlinTest
    implementation kau.Dependencies.kotlin("test-junit")
}
```


## Changelog Generator

In conjunction with [core](/core#changelog-xml), 
the xml changelog can be converted to markdown.

To allow for compilation per build, add your own task:

```gradle
task generateChangelogMd() {
    kau.ChangelogGenerator.generate([inputPath], [outputPath])
}
```

The wrapper allows for the generator to be called automatically with each build.



