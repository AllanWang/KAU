# KAU :imagepicker

ImagePicker is a beautiful gallery activity that allows you to pick images
from your storage. It is backed by FastAdapter and Glide, and offers blur and fade transitions.

`ImagePickerActivity` is already fully functional, so you may directly add it to your manifest.
However, you can also extend it to change the package name.

You may also easily launch the activity through the simple binder:
```
Activity.kauLaunchImagePicker(YourClass::class.java, yourRequestCode)
```

If you are using the built in activity, you may omit the class argument.

Note that this launches the activity through a `startActivityForResult` call

You may get the activity response by overriding your `onActivityResult` method
to first verify that the request code matches and then call `kauOnImagePickerResult`

This module also has a template style `Kau.ImagePicker` that defaults to a slide up animation.