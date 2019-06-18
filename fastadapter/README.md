# KAU :fastadapter

Collection of kotlin bindings and custom IItems for [Fast Adapter](https://github.com/mikepenz/FastAdapter)

## KauIItems

Abstract base that extends `AbstractIItems` and contains the arguments `(layoutRes, ViewHolder lambda, idRes)` in that order. 
Those variables are used to override the default abstract functions.
If a layout is only used for one item, it may also be used as the id, which you may leave blank in this case.
The ViewHolder lambda is typically of the form `::ViewHolder`
Where you will have a nested class `ViewHolder(v: View) : RecyclerView.ViewHolder(v)`

## IItem Templates

* CardIItem - generic all encompassing card item with a title, description, imageview, and button. 
All items except for the title are optional.
* HeaderIItem - simple title container with a big top margin