# Changelog

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
* Port changelog builer
* Port ripple canvas
