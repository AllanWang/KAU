# KAU :core

> The framework of everything

## Contents

* [KPrefs](#kprefs)
* [Changelog XML](#changelog-xml)
* [FAQ XML](#faq-xml)
* [Kotterknife](#kotterknife)
* [Ripple Canvas](#ripple-canvas)
* [MeasureSpecDelegate](#measure-spec-delegate)
* [CollapsibleViewDelegate](#collapsible-view-delegate)
* [Swipe](#swipe)
* [Debounce](#debounce)
* [KAU Logger](#kau-logger)
* [Email Builder](#email-builder)
* [Extension Functions](#extension-functions)
* [Lazy Resettable](#lazy-resettable)

## KPrefs

A typical SharedPreference contains items that look like so:

```Java
class MyPrefs {
    public static final String TEXT_COLOR = "TEXT_COLOR";

    private static SharedPreference prefs = ...

    public static void setTextColor(int color) {
        prefs.edit().putInt(TEXT_COLOR, color).apply();
    }

    public static int getTextColor() {
        prefs.getInt(TEXT_COLOR, Color.WHITE);
    }
}
```
  
KPrefs greatly simplifies it by using Kotlin's object pattern and delegates to achieve the following:

```Kotlin
object MyPrefs : KPref() {
    var textColor: Int by kpref("TEXT_COLOR", Color.WHITE)
    var bgColor: Int by kpref("BG_COLOR", Color.BLACK)
    var isFirstLaunch: Boolean by kpref("IS_FIRST_LAUNCH", true)
    ...
}
```

By using `KPrefSample.textColor = Color.RED` or `textView.setTextColor(KPrefSample.textColor)` we can effectively set and save the value.

The values are retrieved lazily and are only done so once; future retrievals will be done with a local value, and updating a preference will both save it in the SharedPreference and update it locally.

The object inherits the initializer method `fun initialize(c: Context, preferenceName: String)`, which must be invoked (preferably in the Application class) before using a preference.

There is also a `reset()` method to clear the local values and have them retrieve from the SharedPreference again.

In shared preferences, we often require a boolean that returns true once, so we can use it to showcase views or display prompts on the first load.
KPref supports special preferences like these through the `KPrefSingleDelgate`

It can be used in a KPref like so:

```Kotlin
object MyPrefs : KPref() {
    val displayHelper: Boolean by kprefSingle("KEY_HERE")
    ...
}
```

Notice that it is a `val` and takes no default. It will return true the first time and false for all subsequent calls.

## Changelog XML

Create an xml resource with the following structure:

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <version title="v0.1" />
    <item text="Initial Changelog" />
    <item text="Bullet point here" />
    <item text="More points" />
    <item text="" /> <!-- this one is empty and therefore ignored -->
</resources>
```

And show it with `context.showChangelog(@XmlRes xmlRes: Int)`
There is an optional `customize` argument to modify the builder before showing the changelog.

As mentioned, blank items will be ignored, so feel free to create a bunch of empty lines to facilitate updating the items in the future.

Here is a template xml changelog file:

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>

    <!--
    <version title="v"/>
    <item text="" />
    -->

    <version title="v0.1" />
    <item text="Initial Changelog" />
    <item text="" />
</resources>
```

## FAQ XML

There is another parser for a FAQ list with the following format:

```xml
<question>This is a question</question>
<answer>This is an answer</answer>
```

Call `kauParseFaq` and pass a callback taking in a `List<Pair<Spanned, Spanned>` that you can work with.
By default, the questions are numbered, and the content is formatted with HTML. 
You may still need to add your own methods to allow interaction with certain elements such as links.

## Kotterknife

KAU comes shipped with [Kotterknife](https://github.com/JakeWharton/kotterknife) by Jake Wharton.
It is a powerful collection of lazy view bindings that only calls the expensive `findViewById` once.

In KAU, there are also resettable versions (suffixed with `Resettable`) for all bindings.
These variants are weakly held in the private `KotterknifeRegistry` object, and can be used to invalidate the lazy
values through the `Kotterknife.reset` method. This is typically useful for Fragments, as they do not follow
the same lifecycle as Activities and Views.

Note that this is useful for views that have ids in multiple layout files or in `id.xml` files.
Kotlin has another solution, [`kotlin-android-extensions`](https://kotlinlang.org/docs/tutorials/android-plugin.html), which is more convenient.

## Ripple Canvas

Ripple canvas provides a way to create simultaneous ripples against a background color. 
They can be used as transitions, or as a toolbar background to replicate the look for [Google Calendar](https://stackoverflow.com/questions/27872324/how-can-i-animate-the-color-change-of-the-statusbar-and-toolbar-like-the-new-ca)

![Color Picker Gif](https://raw.githubusercontent.com/AllanWang/Storage-Hub/master/kau/kau_color_picker.gif)

Many ripples can be stacked on top of each other to run at the same time from different locations.
The canvas also supports color fading and direct color setting so it can effectively replace any background.

## Measure Spec Delegate

If you ever have a view needing exact aspect ratios with its parent and/or itself, this delegate is here to help.
Implementing this in any view class unlocks its attributes, giving you three layers of view measuring to ensure exact sizing.
More information can be found in the [klass file](https://github.com/AllanWang/KAU/blob/master/core/src/main/kotlin/ca/allanwang/kau/ui/views/MeasureSpecDelegate.kt)

## Collapsible View Delegate

A common animation is having a view that can smoothly enter and exit by changing its height.
This delegate will implement everything for you and give you the methods `expand`, `collapse`, etc.
See the [kclass file](https://github.com/AllanWang/KAU/blob/master/core/src/main/kotlin/ca/allanwang/kau/ui/views/CollapsibleViewDelegate.kt) for more details.

# Swipe

A collection of activity extension methods to easily make any activity swipable:

![Swipe Gif](https://raw.githubusercontent.com/AllanWang/Storage-Hub/master/kau/kau_swipe.gif)

Simply add the following three calls in the appropriate overriding methods:

```kotlin
kauSwipeOnCreate()      //in the onCreate method; you may also pass settings through here
kauSwipeOnPostCreate()  //in the onPostCreate method
kauSwipeOnDestroy()     //in the onDestroy method

kauSwipeFinish()        //optional; replace onBackPressed with this to animate the activity finish
```

For the best results, activities should be translucent. See `Kau.Transparent` for a base style example.

Special thanks goes to the original project, [SwipeBackHelper](https://github.com/Jude95/SwipeBackHelper)

KAU's swipe is a Kotlin rewrite, along with support for all directions and weakly referenced contexts.

# Debounce

Debouncing is a means of throttling a function so that it is called no more than once in a given instance of time.
An example where you'd like this behaviour is the searchview: you want to deliver search results quickly,
but you don't want to update your response with each new character.
Instead, you can wait until a user finishes their query, then search for the results.

Example:

![Debounce 0](https://raw.githubusercontent.com/AllanWang/Storage-Hub/master/kau/kau_search_debounce_0.gif)
![Debounce 500](https://raw.githubusercontent.com/AllanWang/Storage-Hub/master/kau/kau_search_debounce_500.gif)

The first case is an example of no debouncing, whereas the second case is an example with a 500ms debounce.

KAU offers extensions to easily convert or create functions into debouncables.
Simply call `debounce` and specify your interval on an existing function, or with a new function. 


## KAU Logger

`KauLogger` can be extended by an object to implement a logger with a permanent tag.
It's methods are the same as the main logger, along with the following:
* Logging methods have two inputs, the main text and private text. 
If private text is disabled (default), it will not be logged.
* The loggers have toggles to enable logging as a whole and to filter out priorities that we wish to ignore.
* Has some other functions, like logging the current thread.

## Email Builder

Easily send an email through `Context.sendEmail`. 
Include your email and subject, along with other optional configurations such as retrieving device info.

## Extension Functions

> "[Extensions](https://kotlinlang.org/docs/reference/extensions.html) provide the ability to extend a class with new functionality without having to inherit from the class"

KAU's vast collection of extensions is one of its strongest features. 
There are too many to explain here, but you may check out the [utils package](https://github.com/AllanWang/KAU/tree/master/core/src/main/kotlin/ca/allanwang/kau/utils)

## Lazy Resettable

In the spirit of Kotlin's Lazy delegate, KAU supports a resettable version. Calling `lazyResettable` produces the same delegate,
but with an additional `invalidate` method.

To further simplify this, there is also a `LazyResettableRegistry` class that can be used to hold all resettables.
The instance can be passed through the `lazyResettable` method, or classes can provide their own extension functions to 
register the delegates by default.