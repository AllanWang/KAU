# KAU :about

Most apps could not be possible without the collective efforts of other developers and their libraries.
It's always nice to give credit where credit is due, but it's not always at the top of ones agenda.
About Activity aims to fix that by preparing a beautiful overlay activity that does just that.

![About Activity Gif](https://raw.githubusercontent.com/AllanWang/Storage-Hub/master/kau/kau_about_activity.gif)

The overlay is heavily based off of the about section in [Plaid](https://github.com/nickbutcher/plaid),
and adds on the power of [About Libraries](https://github.com/mikepenz/AboutLibraries) to automatically find the dependencies.

This activity can be easily added by extending `AboutActivityBase`.
Everything is already prepared, but you can modify the theme or other components through the config DSL or through the open functions.
There are also numerous iitem cards already prepared (in this submodule and from `:adapter`)if you wish to add that in your main view.

You may also easily launch the activity through the simple binder:
```
Activity.kauLaunchAbout(YourClass::class.java)
```

Be sure to include the activity in your Manifest and have it extend `Kau.About`, or any other style that achieves the same look.

## Proguard

Without auto detect, KAU about will retain the classes containing the lib strings by default.
If you use proguard with auto detect, make sure to retain all R classes to make it possible

```
-keep class .R
-keep class **.R$* {
    <fields>;
}
```