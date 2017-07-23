# KAU :imagepicker

ImagePicker is a beautiful gallery activity that allows you to pick images
from your storage. It is backed by FastAdapter and Glide, and offers blur and fade transitions.

`ImagePickerActivityBase` is already fully functional, so you may directly extend it with no further changes
and add the activity to your manifest

You may also easily launch the activity through the simple binder:
```
Activity.kauLaunchImagePicker(YourClass::class.java, yourRequestCode)
```

Note that this launches the activity through a `startActivityForResult` call

You may get the activity response by overriding your `onActivityResult` method
to first verify that the request code matches and then call `kauOnImagePickerResult`

This module also has a template style `Kau.ImagePicker` that defaults to a slide up animation.