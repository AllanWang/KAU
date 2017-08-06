# KAU :searchview

KAU contains a fully functional SearchView that can be added programmatically with one line. 
It contains a `bindSearchView` extension functions for both activities and viewgroups.

![Search View Gif](https://raw.githubusercontent.com/AllanWang/Storage-Hub/master/kau/kau_search_view.gif)

The searchview is:
* Fully themable - set the foreground or background color to style every portion, from text colors to backgrounds to ripples
* Complete - binding the search view to a menu id will set the menu icon (if not previously set) and attach all the necessary listeners
* Configurable - modify any portion of the inner Config class when binding the search view
* Debouncable - specify a time interval to throttle your queries; see [debouncing](/core#debounce)