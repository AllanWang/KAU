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

## MediaActions

On top of retrieving your media file, you may also add action items to the start
of the grid. All actions will return their results immediately, and retrieve media types based on the activity.

### MediaActionCamera

Gets an image or a video from the default camera. No permissions are necessary.
Note that since api 24, passing general uris may throw a [FileUriExposedException](https://developer.android.com/reference/android/os/FileUriExposedException.html),
so your own resolvers need to be passed for this to work. See the sample xml folder for an example.

### MediaActionCameraVideo

Given that getting videos do not require resolvers, this item can be used for videos only without any required arguments.

### MediaActionGallery

Defines whether you want to pick one or more media items from the default gallery app.