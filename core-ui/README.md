# KAU :core-ui

Holds a collection of generic UIs. 
This submodule takes heavy influence from [Plaid](https://github.com/nickbutcher/plaid), a beautiful showcase for Material Design.

## BoundedCardView

Extends a CardView and provides `maxHeight` and `maxHeightPercentage` attributes.
These values are judged once the view it attached and can be helpful to limit the size with respect to its parent.

One example is in KAU's `:searchview`, where the search results will always allow some space below it for the user to tap and exit.

## CutoutView

Given a background and a text/vector, will "erase" the text/vector from the background.
This can be seen in effect in KAU's `:about` submodule.

## ElasticDragDismissFrameLayout

When scrolling vertically, this frame will allow for overscrolling and will pull the layout out of view and exit if a threshold is reached.
Note that Activities with this frame must be translucent. `@style/Kau.Translucent` can be used as a base.

## InkPageIndicator

A beautiful viewpager indicator

## TextSlider

An animated and themable Text Switcher. Specify its direction and set a new text value and it will slide it into view.