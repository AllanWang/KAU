# Changelog

## v6.2.0
* Add support for Android 4.2 beta03
* Remove all usages of kotlin synthetics
* Many version bumps

## v6.1.0
* Add support for Android 4.1 RC03
* Breaking: Updated iconics, buganizer, and spotless dependencies (major version bump)

## v6.0.0
* Add support for Android 4.1.x
* Add major version library updates (FastAdapter, AboutLibrary, Iconics)

## v5.3.0
* :about: Moved config builder inside activity
* :color: Allow option to disable selected ring
* :core: Breaking KPref changes; see migration

## v5.2.0
* :about: Migrate about libraries to v7.x.x
* :color: Draw CircleView in foreground instead of background
* :core: Migrate iconics to v4.x.x
* :core: Automatically switch light mode for navigationBarColor and statusBarColor
* :core: Remove statusBarLight toggle
* :core: Remove kau_status_bar_height; height should be found programmatically
* :fastadapter: Migrate fastadapter to v4.x.x
* :fastadapter-viewbinding: Create helper items for ViewBinding
* Update translations

## v5.1.0
* :adapter: Moved fastadapter elements to new module, :fastadapter:. To migrate, simply rename the dependency. If you don't use fast adapter, no changes are necessary
* :adapter: Make NoAnimatorChange an object; previously a class
* :core: KPref now supports in memory only variants for testing; pass KPrefBuilderInMemory to KPref constructor
* :core: KPref initializer takes in SharedPreferences so user can configure it

## v5.0.0
* Update Android SDK to 29 and Kotlin to 1.3.31
* Update translations
* :core: Update Material Dialogs to 3.x
* :core: Change ProgressAnimator API
* :core: Remove cursor tinting in EditText as it used reflection
* :colorpicker: Strip down to just the interface; unless you require the accent palette, it may be fine to just use MD's color extension
* :gradle-plugin: Convert to kotlin, rework dependencies, and remove extension hooks

## v4.1.0
* :core: Deprecate NetworkUtils, as the underlying functions are deprecated
* :core: Permission manager no longer synchronized, as all actions should occur in the main thread
* :kpref-activity: Getter and setter now have action context, with the option to reload self

## v4.0.0
* Update translations

## v4.0.0-alpha02
* Update translations
* :core: Remove anko dependency. Methods that used it now use coroutines; see the migration doc for minor changes
* :core: Add default CoroutineScope implementation to KauBaseActivity
* :core: Remove zip class. Coroutines and join can be used as an alternative
* :core: Delete flyweight implementation. Kotlin already has getOrPut
* :core: Introduce ContextHelper, where you can get the default looper, handler, and dispatcher for Android
* :mediapicker: Use video preloading instead of full async loading

## v4.0.0-alpha01
* Migrate to androidx. See migration for external dependency changes.
* :core: Remove deprecation warning for Kotterknife

## v3.8.0
* Update everything to Android Studio 3.1
* Fix new lint issues (see Migration for resource related methods)
* :adapter: Add more IAdapter functions to help retrieve selections
* :core: Deprecate Kotterknife; use kotlin_android_extensions
* :kpref-activity: Fix seekbar increment
* :core: Make KPref use Set<String> vs StringSet

## v3.7.1
* Update appcompat to 27.1.0

## v3.7.0
* :core: Fix potential NPE in restart()
* :core: Create restartApplication()
* :colorpicker: Rewrote implementation to be null and parse safe
* :colorpicker: Added more encapsulation to CircleView (selected -> colorSelected; all others are private)
* :adapter: [Breaking] update fastadapter; click listeners now have nullable views
* Update documentation

## v3.6.3
* :core: Check for tablet in email builder
* :kpref-activity: Simplify internal code and add better encapsulation
* :kpref-activity: Disable seekbar when kprefseekbar is disabled
* Add Chinese, Indonesian, Norwegian, Polish, Thai, and Turkish translations
* Add back git versioning
* Created gradle plugin for getting version updates

## v3.6.2
* :core: Pass null instead of bundle if bundle is empty for startActivity
* :core: Support sending attachments for email
* :core: Create more bundle utils to help with shared transition elements
* :searchview: Add better encapsulation and use view location
* :searchview: Add textClearedCallback

## v3.6.1
* :core: [Breaking] Removed private text field and introduced lazy logging functions
* :adapter: Improve library item layout

## v3.6.0
* :adapter: Create withOnRepeatedClickListener
* :core: Create kotlin flyweight
* :core: Created BundleUtils
* :core: [Breaking] Refactored startActivity functions
* :kpref-activity: [Breaking] Simplified listener function parameters
* :kpref-activity: [Breaking] Added dynamic string loading options
* (See Migrations.md for further details on breaking changes)

## v3.5.1
* Add Portuguese translations
* Add Galician translations
* Add some minor util elements
* Update dependencies (sdk 27)

## v3.5.0
* Update dependencies, many of which with major version increments
* Add Vietnamese translations
* Add Italian translations
* Clean up unnecessary build version support
* Optimize and refactor old code
* :adapter: Add helper methods to enhance FastAdapter for Kotlin
* :core: Create ProgressAnimator class
* :searchview: Add searchview holder interface

## v3.4.5
* Add French translations
* Add Spanish translations
* Add German translations
* Remove unnecessary strings

## v3.4.4
* Add translation support for crowdin
* Update dependencies
* :mediapicker: Validate document uri before parsing
* :searchview: Ignore casing for highlights

## v3.4.3
* :core: Validate context before showing dialogs
* :core: Add intent resolver checks prior to all executions.
* :core: Fix bundle NPE when starting activity
* :kpref-activity: Create timePicker

## v3.4.0
* Update to gradle 4.x; api and implementation rather than compile
* Update dependencies
* :searchview: Ensure reveals are called on the UI thread
* :searchview: Check that searchview has a parent before checking card state
* :mediapicker: Reuse request manager from activity
* :kpref-activity: Add bounds to text item

## v3.3.2
* :kpref-activity: Add visibility toggle to Core contract. Items can override this to show/hide given preferences based on boolean callbacks.
* :kpref-activity: Add width constraint for long text items

## v3.3.1
* :core: Open up all logger functions
* :core: Deprecate kauSwipeOnPostCreate and move functionality to onCreate
* :searchview: Fix background tint

## v3.3.0
* :core: Create debounce methods
* :core: Create zip methods
* :core: [Breaking] Logging base has been renamed to KauLogger and no longer depends on timber
* :kpref-activity: Rewrote binding logic to use only one recyclerview
* :kpref-activity: [Breaking] Removed sliding toolbar and use normal toolbar title
* :kpref-activity: Remove :core-ui: dependency
* :searchview: [Breaking] remove reactive dependencies and stick with basic callbacks

## v3.2.5
* :core: Fix FAQ background
* :core: Create FileUtils
* :core: Create NotificationUtils
* :core: Update swipe to remove most exceptions
* :core: Make logging class functions inline
* :core: Create removeIf for mutableIteratables
* :core-ui: Move reactive libs to :searchview:

## v3.2.3
* :about: Modularize everything
* :about: Create FAQ panel
* :core: Create FAQ parser
* :core: Create collapsible view delegate
* :mediapicker: Allow for prefetching by default for videos

## v3.2.2
* :core: Add simple KauBaseActivity so that activities extending AppCompatActivity can have some default kau helpers implemented
* :core: The permission manager will now notify you if you try to request a permission that isn\'t added to your manifest
* Begin writing android tests

## v3.2.1
* :core: Remove requestLayout call from setMargin and setPadding
* :core: Fix kau direction bits
* :core: Greatly simplify ripple canvas and truly support transparent ripples
* :core: Generalize fab transition to fade scale transition for all imageviews
* :core: Create ViewPropertyAnimator.scaleXY() and View.scaleXY
* :core: Create View.setOnSingleTapListener()
* :core: Create rndColor, which generates a random opaque color for testing
* :core: Add resettable view binding variants to Kotterknife
* :core: Create lazy resettable registry
* :core: Add more transitions and anims
* :kpref-activity: Reduce alpha color for desc
* :imagepicker: [breaking] Rename to mediapicker and add support for videos (alpha)

## v3.2.0
* :adapter: Make KauAnimator extensible
* :imagepicker: Add uri val to ImageModel
* :imagepicker: Create bindings and overlay activity
* :imagepicker: Create single image picker counterpart with overlay
* :searchview: Remove item animator so nonchanging items don\'t blink
* Add showcase app to play store
* Update build tools to 26.0.1
* Update dependencies

## v3.1.0
* :core: Allow for nullable throwables when logging
* :core: Remove some extra DSL annotations
* :kpref-activity: Bring down to minSdk 19 and fix compatibility
* :adapter: Update readme for iitems and animators
* :about: Move strings to private

## v3.0
* :core: Add setPadding[x]
* :core: [breaking] Replace update[x]Margin to setMargin[x]
* :imagepicker: Fully implement picker
* Make resources private where possible
* Reduce minSdk to 19 where possible

## v2.1
* :adapter: Fix up CardIItem
* :adapter: Modularized kau animators
* :adapter: Switched from mutablelist to list inputs for themed animator
* :core-ui: Create ElasticRecyclerActivity
* :core-ui: Create MeasuredImageView
* :core: Create MeasureSpecDelegate
* :core: Improve PermissionManager logging
* :core: Inline all util variables with getters
* :core: Introduce fade animation style templates
* :core: Introduce slide transition style templates
* :core: Update utils and remove StringHolder
* :imagepicker: Create full image picker with blurrable selections
* Update dependencies

## v2.0
* Huge refactoring to separate functions to their own submodules
* Huge Docs update
* Reorder KauIItem arguments to support optional id
* Create KPrefSeekbar, which binds an int kpref to a seekbar
* Open all kpref item binders so they may be extended
* Fix scrolling issue on about dismiss
* Make rClass optional in about activity

## v1.5
* Change snackbar builder
* Change addBundle to withArguments to match ANKO
* Create KauIItem to replace AbstractItem
* Create permission manager and permission constants
* Create swipe, a very simple helper to allow for activities to be dismissed with gestures
* Create network utils

## v1.4
* Add about activities
* Add themed fast item imageAdapter
* Add chained imageAdapter
* Add item animators
* Port some views over from Plaid
* Add string arg option for sendEmail
* Add many iitems

## v1.3
* Add kpref subitems
* Add DSL markers
* Add transition utils and other utils
* Add custom searchview with binders
* Add KauBoundedCardView

## v1.2
* Fix title attribute in changelog
* Update support libs
* Add is app installed utils
* Add email builder

## v1.1
* Create kpref items
* Attach source files
* Create color dialog
* Add more utilities
* Fix indexStack clearing when starting activity

## v1.0
* Initial Changelog
* Create many extension functions
* Port changelog builder
* Port ripple canvas
