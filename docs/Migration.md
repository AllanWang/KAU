# Migrations

Below are some highlights on major refactoring/breaking changes

# v4.0.1-alpha02

* `kauParseFaq` is now synchronous. 

## Anko has been removed

A lot of the methods are already implemented in KAU, and it was primarily imported for its `doAsync` methods. Now, they have been replaced with coroutines.
Some methods have been copied over:

* import org.jetbrains.anko.runOnUiThread > import ca.allanwang.kau.utils.runOnUiThread
* import org.jetbrains.anko.contentView > import ca.allanwang.kau.utils.contentView
* import org.jetbrains.anko.bundleOf > import ca.allanwang.kau.utils.bundleOf

# v4.0.0-alpha01

This is the first introduction of androidx. The goal is to just do a migration with minimal changes.
Nothing has been changed internally, but the dependencies are updated.
Notably, Android-Iconics [split their Community Icons](https://github.com/mikepenz/Android-Iconics/blob/develop/MIGRATION.md) into two enums.

Kotterknife is also no longer deprecated. There are some use cases where `kotlin-android-extensions` isn't the best, such as when multiple layout files are used, or when ids are defined in the id.xml.
It is still recommended to use the extension where applicable.

# v3.8.0

Along with the update to support Android Studio 3.1, a lot of changes have occurred with other dependencies and with lint.

## Invalid Resource Id Equality

Resource ids can be negatives due to the bit overflow. 
Instead, `INVALID_ID` has been introduced to signify an unset or invalid id.
Methods such as `Context.string(id, fallback)` now check against `INVALID_ID` through equality rather than using an inequality to address this.

## Deprecate Kotterknife

Kotterknife has been deprecated in favour of `kotlin-android-extensions`. 
See [official docs](https://kotlinlang.org/docs/tutorials/android-plugin.html#view-binding).

## Update KPref

KPref has been slightly refactored internally. 
Preferences backed by `StringSet` can now go back to `Set<String>`

# v3.6.0

## startActivity

Before, startActivity attempted to bind a lot of options with defaults.
Instead, we will now rely on the builder pattern so users may supply their own.
Attributes like `transition` have been replaced with bundle functions such as `withSceneTransitionAnimation(context)`.
The ordering of the builder functions have also been unified so that `bundleBuilder` is always before `intentBuilder`.

## kpref-activity

### Click Events

Instead of passing parameters through the click functions, which were often unused,
they will now be provided through extensions from `KClick`.

`KClick` holds the same values you'd expect (`itemView`, `innerView` (renamed), `item`),
and adds on `context` and is loaded lazily where possible.

### Title Res

In an attempt to make kprefs functional and thus easy to configure,
two new functions, `titleFun` and `descFun` have been introduced.
They will be triggered whenever kprefs are updated to get an up to date stringRes
based on whatever conditions you specify. Most conditions are passed through anyways,
which is why these functions supply no additional information.

You are still free to use the original `descRes`
and the constructor title, which has been renamed to `titleId` to emphasis its immutability.
Reloading kprefs are always done through the original `titleId`, 
regardless of the actual resource currently used. 