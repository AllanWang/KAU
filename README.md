<a href="https://allanwang.github.io/KAU/" target="_blank">
<img src="https://cdn.rawgit.com/AllanWang/KAU/master/files/images/logo.svg" alt="KAU" width="30%"/>
</a>

An extensive collection of <b>Kotlin Android Utils</b>

This library contains small helper functions used throughout almost all of my other projects. The goal is to make common interactions executable in a single line.

<a href='https://play.google.com/store/apps/details?id=ca.allanwang.kau.sample&utm_source=github'><img alt='Get it on Google Play' width="30%" src='https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png'/></a>

[Changelog](docs/Changelog.md)

------------

KAU is available on JitPack

[![](https://jitpack.io/v/ca.allanwang/kau.svg)](https://jitpack.io/#ca.allanwang/kau) 
[![Build Status](https://travis-ci.org/AllanWang/KAU.svg?branch=master)](https://travis-ci.org/AllanWang/KAU)
[![Awesome Kotlin Badge](https://kotlin.link/awesome-kotlin.svg)](https://github.com/KotlinBy/awesome-kotlin)
[![Stories in Ready](https://badge.waffle.io/AllanWang/KAU.png?label=ready&title=Ready)](https://waffle.io/AllanWang/KAU?utm_source=badge)
[![GitHub license](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://raw.githubusercontent.com/AllanWang/KAU/master/LICENSE)

To apply, add the following to your root build.gradle:

```gradle
allprojects {
    repositories {
        ...
        jcenter()
        maven { url "https://jitpack.io" }
        maven { url "https://maven.google.com" }  //for gradle < 4.0
        google()                                  //for gradle >= 4.0
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
    compile "ca.allanwang.kau:kpref-activity:$KAU"
    compile "ca.allanwang.kau:mediapicker:$KAU"
    compile "ca.allanwang.kau:searchview:$KAU"
}

```

-----------

# Submodules 
> Linked to their respective docs.<br/>
> Included dependencies are only those with exposed APs; see [new dependency configurations](https://developer.android.com/studio/build/gradle-plugin-3-0-0-migration.html#new_configurations).<br/>
> Implemented dependencies are wrapped in parentheses.

## [Core](core#readme)
* Collection of extension functions and small helper methods applicable in almost any application.
* Notable features: 
  * [KPrefs](core#kprefs)
  * [Changelog XML](core#changelog-xml)
  * [FAQ XML](core#faq-xml)
  * [Kotterknife](core#kotterknife)
  * [Ripple Canvas](core#ripple-canvas)
  * [MeasureSpecDelegate](core#measure-spec-delegate)
  * [CollapsibleViewDelegate](core#collapsible-view-delegate)
  * [Swipe](core#swipe)
  * [Debounce](core#debounce)
  * [KAU Logger](core#kau-logger)
  * [Email Builder](core#email-builder)
  * [Extension Functions](core#extension-functions)
  * [Lazy Resettable](core#lazy-resettable)
* Includes 
[`AppCompat`](https://developer.android.com/topic/libraries/support-library/index.html), 
[`Material Dialogs (core)`](https://github.com/afollestad/material-dialogs),
[`Iconics`](https://github.com/mikepenz/Android-Iconics), 
[`Anko`](https://github.com/Kotlin/anko),
[`Kotlin stdlib`](https://kotlinlang.org/api/latest/jvm/stdlib/)

## [Core UI](core-ui#readme)
* Collection of complex views and widgets
* Includes `:core`, `:adapter`

## [About](about#readme)
* Modularized overlaying about section. Comes with a main panel, automatic lib detection, and a FAQ parser; also includes the lib strings for KAU.
* Includes `:core-ui`, `:adapter`, 
[`About Libraries`](https://github.com/mikepenz/AboutLibraries)

## [Adapter](adapter#readme)
* Kotlin bindings for the fast adapter, as well as modularized RecyclerView animations
* Includes `:core`, 
[`Fast Adapter`](https://github.com/mikepenz/FastAdapter)

## [Color Picker](colorpicker#readme)
* Implementation of a color picker dialog with subtle transitions and a decoupled callback
* Includes `:core`, 
([`Material Dialogs (commons)`](https://github.com/afollestad/material-dialogs))

## [KPref Activity](kpref-activity#readme)
* Fully programmatic implementation of a Preference Activity, backed by a RecyclerView
* Includes `:core`, `:adapter`, `:colorpicker`

## [Media Picker](mediapicker#readme)
* Fully functional image and video pickers, both as an overlay and as a requested activity.
* Includes `:core-ui`, 
[`Glide`](https://github.com/bumptech/glide),
([`Blurry`](https://github.com/wasabeef/Blurry))

## [SearchView](searchview#readme)
* Material searchview with kotlin bindings
* Includes `:core-ui`, `:adapter`

-----------

# Showcase

![About Activity Gif](https://raw.githubusercontent.com/AllanWang/Storage-Hub/master/kau/kau_about_activity.gif)
![Ink Indicator Gif](https://raw.githubusercontent.com/AllanWang/Storage-Hub/master/kau/kau_ink_indicator.gif)
![Color Picker Gif](https://raw.githubusercontent.com/AllanWang/Storage-Hub/master/kau/kau_color_picker.gif)
![Color Picker Custom Gif](https://raw.githubusercontent.com/AllanWang/Storage-Hub/master/kau/kau_color_picker_custom.gif)
![KPref Items Gif](https://raw.githubusercontent.com/AllanWang/Storage-Hub/master/kau/kau_kpref_items.gif)
![SearchView Gif](https://raw.githubusercontent.com/AllanWang/Storage-Hub/master/kau/kau_search_view.gif)
![Swipe Gif](https://raw.githubusercontent.com/AllanWang/Storage-Hub/master/kau/kau_swipe.gif)

# Proguard/MultiDex

Given that the core module contains a lot of extension functions, you may run into a dex error (over 64k methods)

To resolve that, add `multiDexEnabled true` under your `app.gradle > android > defaultConfig`

Likewise, it is highly recommended to use proguard to clean up your project upon release.
All KAU components support proguard out of the box. 
Some may have extra requirements for certain features, which will be detailed in their respective README.