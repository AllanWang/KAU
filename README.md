<img src="https://cdn.rawgit.com/AllanWang/KAU/master/files/images/logo.svg" alt="KAU" width="30%"/>

Kotlin Android Utils

This library contains small helper functions used throughout almost all of my other projects. The goal is to make common interactions executable in a single line.

<a href='https://play.google.com/store/apps/details?id=ca.allanwang.kau.sample&utm_source=github'><img alt='Get it on Google Play' width="30%" src='https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png'/></a>

[Changelog](docs/Changelog.md)

------------

KAU is available on JitPack

[![](https://jitpack.io/v/ca.allanwang/kau.svg)](https://jitpack.io/#ca.allanwang/kau) 
[![Build Status](https://travis-ci.org/AllanWang/KAU.svg?branch=master)](https://travis-ci.org/AllanWang/KAU)
[![GitHub license](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://raw.githubusercontent.com/AllanWang/KAU/master/LICENSE)

To apply, add the following to your root build.gradle:

```gradle
allprojects {
    repositories {
        ...
        jcenter()
        maven { url "https://jitpack.io" }
        maven { url "https://maven.google.com" }
    }
}
```

And add the following dependencies (You can use a specific version, commit, or -SNAPSHOT):

Note that only `core` is required if you want the basic features.
Note that if you use any particular submodule, it will automatically include all of its necessary dependencies.

```gradle
dependencies {
    //All submodules extend this
    compile "ca.allanwang.kau:core:$KAU"
    //All submodules with extensive ui extend this
    compile "ca.allanwang.kau:core-ui:$KAU"
    
    compile "ca.allanwang.kau:about:$KAU"
    compile "ca.allanwang.kau:colorpicker:$KAU"
    compile "ca.allanwang.kau:mediapicker:$KAU"
    compile "ca.allanwang.kau:kpref-activity:$KAU"
    compile "ca.allanwang.kau:searchview:$KAU"
}

```

-----------

# Submodules 
(linked to their respective Docs)

## [Core](core#readme)
* Collection of extension functions and small helper methods applicable in almost any application.
* Notable features: KPrefs, Changelog XML, Ripple Canvas, Extensions, Email Builder
* Includes 
[`AppCompat`](https://developer.android.com/topic/libraries/support-library/index.html), 
[`Material Dialogs (core)`](https://github.com/afollestad/material-dialogs),
[`Iconics`](https://github.com/mikepenz/Android-Iconics), 
[`Anko`](https://github.com/Kotlin/anko),
[`Timber`](https://github.com/JakeWharton/timber), 
[`Kotlin stdlib`](https://kotlinlang.org/api/latest/jvm/stdlib/)

## [Core UI](core-ui#readme)
* Collection of complex views and widgets
* Includes `:core`, `:adapter`, 
[`RxJava`](https://github.com/ReactiveX/RxJava), 
[`RxAndroid`](https://github.com/ReactiveX/RxAndroid), 
[`RxKotlin`](https://github.com/ReactiveX/RxKotlin), 
[`RxBinding`](https://github.com/JakeWharton/RxBinding)

## [About](about#readme)
* Implementation of an overlaying about section, along with automatic lib detection; also includes the lib strings for KAU
* Includes `:core-ui`, `:adapter`, 
[`About Libraries`](https://github.com/mikepenz/AboutLibraries)

## [Adapter](adapter#readme)
* Kotlin bindings for the fast adapter, as well as RecyclerView animations
* Includes `:core`, 
[`Fast Adapter`](https://github.com/mikepenz/FastAdapter)

## [Color Picker](colorpicker#readme)
* Implementation of a color picker dialog with subtle transitions and a decoupled callback
* Includes `:core`, 
[`Material Dialogs (commons)`](https://github.com/afollestad/material-dialogs)

## [Media Picker](mediapicker#readme)
* Fully functional image and video pickers, both as an overlay and as a requested activity.
* Includes `:core-ui`, 
[`Glide`](https://github.com/bumptech/glide),
[`Blurry`](https://github.com/wasabeef/Blurry)

## [Kpref Activity](kpref-activity#readme)
* Fully programmatic implementation of a Preference Activity, backed by RecyclerViews
* Includes `:core-ui`, `:adapter`, `colorpicker`

## [SearchView](searchview#readme)
* Material searchview with kotlin bindings
* Includes `:core-ui`, `:adapter`

-----------

# Showcase

![About Activity Gif](https://raw.githubusercontent.com/AllanWang/Storage-Hub/master/kau/kau_about_activity.gif)
![Ink Indicator Gif](https://raw.githubusercontent.com/AllanWang/Storage-Hub/master/kau/kau_ink_indicator.gif)
![Color Picker Gif](https://raw.githubusercontent.com/AllanWang/Storage-Hub/master/kau/kau_color_picker.gif)
![KPref Items Gif](https://raw.githubusercontent.com/AllanWang/Storage-Hub/master/kau/kau_kpref_items.gif)
![SearchView Gif](https://raw.githubusercontent.com/AllanWang/Storage-Hub/master/kau/kau_search_view.gif)

# Proguard/MultiDex

Given that the core module contains a lot of extension functions, you may run into a dex error (over 64k methods)

To resolve that, add `multiDexEnabled true` under your `app.gradle > android > defaultConfig`

Likewise, it is highly recommended to use proguard to clean up your project upon release.
All KAU components support proguard out of the box. 
Some may have extra requirements for certain features, which will be detailed in their respective README.