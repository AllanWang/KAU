# KAU :imagepicker

ImagePicker is a beautiful gallery activity that allows you to pick images
from your storage. It is backed by FastAdapter and Glide, and stems from the ImagePickerCore model

Currently, there are two options:

--------------------------------

## ImagePickerActivityBase

A full screen multi image picker with beautiful animations. 
Images are blurred when selected, and there is a counter on the top right.
There is a FAB to send back the response.

`R.style.Kau.ImagePicker` is added for your convenience.

## ImagePickerActivityOverlayBase

This overlaying activity makes use of transitions and nested scrolling, and is only for Lollipop and up.
Only one image can be selected, so the overlay exists immediately upon the first selection.
Having this model also means that each item is only one simple image, as opposed to the blurrable image view above.
As a result, this activity has faster loading on scrolling.

`R.style.Kau.ImagePicker.Overlay` is added for your convenience.

--------------------------------

Both activities work out of the box and can be extended without needing further modifications.
Their convenience styles default to a slide in slide out animation from the bottom edge.

You may also easily launch either activity through the simple binder:
```
Activity.kauLaunchImagePicker(YourClass::class.java, yourRequestCode)
```

Note that this launches the activity through a `startActivityForResult` call

You may get the activity response by overriding your `onActivityResult` method
to first verify that the request code matches and then call `kauOnImagePickerResult`,
which will return the list of ImageModels.