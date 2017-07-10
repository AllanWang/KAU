# KAU :adapter

Collection of kotlin bindings and custom IItems for [Fast Adapter](https://github.com/mikepenz/FastAdapter)

## KauIItems

Extends `AbstractIItems` and contains the arguments (layoutRes, ViewHolder lambda, idRes)
In that order. Those variables are used to override the default abstract functions.
If a layout is only used for one item, it may also be used as the id, which you may leave blank in this case.
The ViewHolder lambda is typically of the form `{ ViewHolder(it) }`
Where you will have a nested class `ViewHolder(v: View) : RecyclerView.ViewHolder(v)`