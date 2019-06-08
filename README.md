<a href="https://allanwang.github.io/KAU/" target="_blank">
<img src="https://cdn.rawgit.com/AllanWang/KAU/master/files/images/logo.svg" alt="KAU" width="30%"/>
</a>

An extensive collection of <b>Kotlin Android Utils</b>

This library contains small helper functions used throughout almost all of my other projects. The goal is to make common interactions executable in a single line.

<a href='https://play.google.com/store/apps/details?id=ca.allanwang.kau.sample&utm_source=github'><img alt='Get it on Google Play' width="30%" src='https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png'/></a>

[Changelog](docs/Changelog.md)
[Migration](docs/Migration.md)

------------

KAU is available on JitPack

[![](https://jitpack.io/v/ca.allanwang/kau.svg)](https://jitpack.io/#ca.allanwang/kau) 
[![Build Status](https://travis-ci.com/AllanWang/KAU.svg?branch=dev)](https://travis-ci.com/AllanWang/KAU)
[![Crowdin](https://d322cqt584bo4o.cloudfront.net/kotlin-android-utils/localized.svg)](https://crowdin.com/project/kotlin-android-utils)
[![Awesome Kotlin Badge](https://kotlin.link/awesome-kotlin.svg)](https://github.com/KotlinBy/awesome-kotlin)
[![ZenHub](https://img.shields.io/badge/Shipping%20faster%20with-ZenHub-45529A.svg)](https://app.zenhub.com/workspace/o/allanwang/kau/boards)
[![GitHub license](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://raw.githubusercontent.com/AllanWang/KAU/master/LICENSE)

To apply, add the following to your root build.gradle:

```gradle
allprojects {
    repositories {
        ...
        jcenter()
        maven { url "https://jitpack.io" }
        google()
    }
}
```

And add the following dependencies (You can use a specific version, commit, or -SNAPSHOT):

Note that only `core` is required if you want the basic features.
Note that if you use any particular submodule, it will automatically include all of its necessary dependencies.

```gradle
dependencies {
    // all submodules extend this
    implementation "ca.allanwang.kau:core:$KAU"
    // all submodules with extensive ui extend this
    implementation "ca.allanwang.kau:core-ui:$KAU"
    
    implementation "ca.allanwang.kau:about:$KAU"
    implementation "ca.allanwang.kau:colorpicker:$KAU"
    implementation "ca.allanwang.kau:kpref-activity:$KAU"
    implementation "ca.allanwang.kau:mediapicker:$KAU"
    implementation "ca.allanwang.kau:searchview:$KAU"
}
```

-----------

# Submodules 
> Linked to their respective docs.<br/>
> Included dependencies are only those with exposed APIs; see [new dependency configurations](https://developer.android.com/studio/build/gradle-plugin-3-0-0-migration.html#new_configurations).<br/>
> Implemented external dependencies are wrapped in parentheses.
> All KAU submodule dependencies are implemented, with the exception of `core` in `core-ui`.
This means that you'll need to explicitly include each submodule you'd like to use, even if another declared submodule depends on it.

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
[`AndroidX Components`](https://developer.android.com/topic/libraries/support-library/index.html), 
[`Material Dialogs (core)`](https://github.com/afollestad/material-dialogs),
[`Iconics`](https://github.com/mikepenz/Android-Iconics), 
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

## [Gradle Plugin](buildSrc#readme)
* Gradle plugin to help facilitate versioning and other gradle functions

-----------

# Showcase

![About Activity Gif](https://raw.githubusercontent.com/AllanWang/Storage-Hub/master/kau/kau_about_activity.gif)
![Ink Indicator Gif](https://raw.githubusercontent.com/AllanWang/Storage-Hub/master/kau/kau_ink_indicator.gif)
![Color Picker Gif](https://raw.githubusercontent.com/AllanWang/Storage-Hub/master/kau/kau_color_picker.gif)
![KPref Items Gif](https://raw.githubusercontent.com/AllanWang/Storage-Hub/master/kau/kau_kpref_items.gif)
![SearchView Gif](https://raw.githubusercontent.com/AllanWang/Storage-Hub/master/kau/kau_search_view.gif)
![Swipe Gif](https://raw.githubusercontent.com/AllanWang/Storage-Hub/master/kau/kau_swipe.gif)

# Proguard/MultiDex

Given that the core module contains a lot of extension functions, you may run into a dex error (over 64k methods)

To resolve that, add `multiDexEnabled true` under your `app.gradle > android > defaultConfig`

Likewise, it is highly recommended to use proguard to clean up your project upon release.
All KAU components support proguard out of the box. 
Some may have extra requirements for certain features, which will be detailed in their respective README.

## Translations

KAU depends on translations crowdsourced by the general public.
If you would like to contribute, please visit [here](https://crwd.in/kotlin-android-utils)

Special thanks to the following awesome people for translating significant portions of KAU!

| Language | Contributors |
|----------|--------------|
| Chinese (Simplified) | [Alcatelia](https://crowdin.com/profile/Alcatelia) |
| Chinese (Traditional) | [yipinghuang](https://crowdin.com/profile/yipinghuang) &bull; [Su, Jun-Ming](https://crowdin.com/profile/sujunmin) &bull; [Wei](https://crowdin.com/profile/wei4green) |
| French | [Vincent Kulak](https://github.com/VonOx) &bull; [Jean-Philippe Gravel](https://crowdin.com/profile/wokija) |
| Galician | [Xesús M. Mosquera](https://twitter.com/xesusmmc?lang=en) |
| German | [Bushido1992](https://forum.xda-developers.com/member.php?u=5179246) &bull; [Marcel Soehnchen](https://crowdin.com/profile/msoehnchen) &bull; [3LD0mi HA](https://forum.xda-developers.com/member.php?u=5860523) |
| Indonesian | [M. Angga Ariska](https://www.youtube.com/channel/UCkqMw81s2aw7bYO-U2YhD7w) |
| Italian | [Bonnee](https://github.com/Bonnee) |
| Korean | [잇스테이크](https://crowdin.com/profile/bexco2010) |
| Norwegian | |
| Polish | [pantinPL](https://crowdin.com/profile/pantinPL) |
| Portuguese | [Sérgio Marques](https://crowdin.com/profile/smarquespt) &bull; [Miguel Dos Reis](https://crowdin.com/profile/siersod) |
| Portuguese (Brazilian) | [TheusKhan](https://crowdin.com/profile/TheusKhan) |
| Serbian | [Nikola Radmanović](https://crowdin.com/profile/nikoladradmanovic) |
| Spanish | [Jahir Fiquitiva](https://jahirfiquitiva.me/) &bull; [Nefi Salazar](https://plus.google.com/u/0/105547968033551087431)|
| Thai | [Thanawat Hanthong](https://crowdin.com/profile/peet6015) |
| Turkish | [zuma17](https://crowdin.com/profile/zuma17) |
| Vietnamese | [Alienz](https://crowdin.com/profile/alienyd) |

The full activity stream for the translations can be found [here](https://crowdin.com/project/kotlin-android-utils/activity_stream)

