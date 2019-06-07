# KAU :colorpicker

As of Material Dialog 2.x, `:colorpicker` is effectively a very thin wrapper around [Material Dialog's color picker](https://github.com/afollestad/material-dialogs/blob/master/documentation/COLOR.md).
The main difference is that it exposes an interface internal to KAU, which allows a greater level of consistency within other submodules.
It also contains an extra palette for accent colors.

To use it, call `MaterialDialog.kauColorChooser` and specify the configs.

![Color Picker Gif](https://raw.githubusercontent.com/AllanWang/Storage-Hub/master/kau/kau_color_picker.gif)
