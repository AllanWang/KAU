# Migrations

Below are some highlights on major refactoring/breaking changes

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