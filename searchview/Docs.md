# KAU :searchview

KAU contains a fully functional SearchView that can be added programmatically with one line. 
It contains a `bindSearchView` extension functions for both activities and viewgroups.

![Search View Gif](https://github.com/AllanWang/Storage-Hub/blob/master/kau/kau_search_view.gif)

The search view is:
* Fully themable - set the foreground or background color to style every portion, from text colors to backgrounds to ripples
* Complete - binding the search view to a menu id will set the menu icon (if not previously set) and attach all the necessary listeners
* Configurable - modify any portion of the inner Config class when binding the search view
* Thread friendly - the search view is built with observables and emits values in a separate thread, 
which means that you don't have to worry about long processes in the text watcher. Likewise, all adapter changes are automatically done on the ui thread.
