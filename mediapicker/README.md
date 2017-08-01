# KAU :mediapicker

MediaPicker is a beautiful collection of gallery activities that allow you to pick images or videos
from your storage. It is backed by FastAdapter and Glide, and stems from the PickerCore model.


Currently, there are two options:
Each takes in a MediaType argument, to specify whether it queries images or videos

--------------------------------

## MediaPickerActivityBase

A full screen multi media picker with beautiful animations. 
Items are blurred when selected, and there is a counter on the top right.
There is a FAB to send back the response.

`R.style.Kau.MediaPicker` is added for your convenience.

## MediaPickerActivityOverlayBase

This overlaying activity makes use of transitions and nested scrolling, and is only for Lollipop and up.
Only one item can be selected, so the overlay exists immediately upon the first selection.
Having this model also means that each item is only one simple image, as opposed to the blurrable image view above.
As a result, this activity has faster loading on scrolling.

`R.style.Kau.MediaPicker.Overlay` is added for your convenience.

--------------------------------

Both activities work out of the box and can be extended without needing further modifications.
Their convenience styles default to a slide in slide out animation from the bottom edge.

You may also easily launch either activity through the simple binder:
```
Activity.kauLaunchMediaPicker(YourClass::class.java, yourRequestCode)
```

Note that this launches the activity through a `startActivityForResult` call

You may get the activity response by overriding your `onActivityResult` method
to first verify that the request code matches and then call `kauOnMediaPickerResult`,
which will return the list of MediaModels.