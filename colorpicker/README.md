# KAU :colorpicker

Material Dialogs by default contains a color picker, but it requires an activity that also implements the color callback.
KAU's colorpicker decouples the two, so it only needs a context and a separate callback.
The color picker also animates the selection, and uses Kotlin's DSL to provide easy calling.

To use it, call `Context.colorPickerDialog` and specify and configs as required through the builder.

![Color Picker Gif](https://raw.githubusercontent.com/AllanWang/Storage-Hub/master/kau/kau_color_picker.gif)
![Color Picker Custom Gif](https://raw.githubusercontent.com/AllanWang/Storage-Hub/master/kau/kau_color_picker_custom.gif)