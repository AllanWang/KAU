# KAU

Kotlin Android Utils

This library contains small helper functions used throughout almost all of my other projects. The goal is to make common interactions executable in a single line.

[Changelog](https://github.com/AllanWang/KAU/tree/master/docs/Changelog.md)

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
        maven { url "https://jitpack.io" }
    }
}
```

And add the following dependencies (You can use a specific version, commit, or -SNAPSHOT):

```gradle
dependencies {
    
    compile "ca.allanwang:kau:$KAU"
  
}

```

The dependency above will give the full KAU package in all its glory.
However, if you wish to selectively choose what you want, most features are split into submodules.
More information about each module can be found below and in their respective directories. 

```gradle
dependencies {
    
    //All submodules extend this
    compile "ca.allanwang.kau:core:$KAU"
    //All submodules with extensive ui extend this
    compile "ca.allanwang.kau:core-ui:$KAU"
    
    compile "ca.allanwang.kau:about:$KAU"
    compile "ca.allanwang.kau:colorpicker:$KAU"
    compile "ca.allanwang.kau:imagepicker:$KAU"
    compile "ca.allanwang.kau:kpref-activity:$KAU"
    compile "ca.allanwang.kau:searchbar:$KAU"
}

```

-----------

# Submodules (linked to their respective Docs)

## [Core](/core/Docs.md)
* Collection of extension functions and small helper methods applicable in almost any application.
* Notable features: KPrefs, Changelog XML, Ripple Canvas, Extensions, Email Builder
* Includes AppCompat, Material Dialogs (core), Iconics, Anko, Timber, and Kotlin stlib

## [Core UI](/core-ui/Docs.md)
* Collection of complex views and widgets
* Includes `:core`, `:adapter`, Reactive libs (Java, Android, Kotlin)

## [About](/about/Docs.md)
* Implementation of an overlaying about section, along with automatic lib detection; also includes the lib strings for KAU
* Includes `:core-ui`, `:adapter`, About Libraries

## [Adapter](/adapter/Docs.md)
* Kotlin bindings for the fast adapter, as well as RecyclerView animations
* Includes `:core`, Fast Adapter

## [Color Picker](/colorpicker/Docs.md)
* Implementation of a color picker dialog with subtle transitions and a decoupled callback
* Includes `:core`, Material Dialogs (commons)

## [Image Picker](/imagepicker/Docs.md)
* WIP - Overlaying media chooser
* Includes `:core-ui`, Glide

## [Kpref Activity](/kpref-activity/Docs.md)
* Fully programmatic implementation of a Preference Activity, backed by RecyclerViews
* Includes `:core-ui`, `:adapter`, `colorpicker`

## [Searchbar](/searchbar/Docs.md)
* Material searchbar with kotlin bindings
* Includes `:core-ui`, `:adapter`

-----------

# Showcase

![About Activity Gif](https://github.com/AllanWang/Storage-Hub/blob/master/kau/kau_about_activity.gif)
![KPref Accent Gif](https://github.com/AllanWang/Storage-Hub/blob/master/kau/kau_kpref_accent.gif)
![KPref Items Gif](https://github.com/AllanWang/Storage-Hub/blob/master/kau/kau_kpref_items.gif)
![SearchView Gif](https://github.com/AllanWang/Storage-Hub/blob/master/kau/kau_search_view.gif)