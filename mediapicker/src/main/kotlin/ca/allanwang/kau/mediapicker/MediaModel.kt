/*
 * Copyright 2018 Allan Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ca.allanwang.kau.mediapicker

import android.database.Cursor
import android.database.SQLException
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import android.provider.MediaStore
import androidx.annotation.NonNull
import java.io.File

/** Created by Allan Wang on 2017-07-14. */
data class MediaModel(
  val data: String,
  val mimeType: String,
  val size: Long,
  val dateModified: Long,
  val displayName: String?
) : Parcelable {

  @Throws(SQLException::class)
  constructor(
    @NonNull cursor: Cursor
  ) : this(
    cursor.getString(0),
    cursor.getString(1) ?: "",
    cursor.getLong(2),
    cursor.getLong(3),
    cursor.getString(4)
  )

  constructor(
    f: File
  ) : this(
    f.absolutePath,
    f.extension, // this isn't a mime type, but it does give some info
    f.length(),
    f.lastModified(),
    f.nameWithoutExtension
  )

  constructor(
    parcel: Parcel
  ) : this(
    parcel.readString()!!,
    parcel.readString()!!,
    parcel.readLong(),
    parcel.readLong(),
    parcel.readString()
  )

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeString(this.data)
    parcel.writeString(this.mimeType)
    parcel.writeLong(this.size)
    parcel.writeLong(this.dateModified)
    parcel.writeString(this.displayName)
  }

  val isGif
    get() = mimeType.endsWith("gif")

  val isImage
    get() = mimeType.endsWith("image")

  val isVideo
    get() = mimeType.endsWith("video")

  val uri: Uri by lazy { Uri.fromFile(File(data)) }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Parcelable.Creator<MediaModel> {
    @Suppress("DEPRECATION")
    // TODO verify data deprecation
    val projection =
      arrayOf(
        MediaStore.MediaColumns.DATA,
        MediaStore.MediaColumns.MIME_TYPE,
        MediaStore.MediaColumns.SIZE,
        MediaStore.MediaColumns.DATE_MODIFIED,
        MediaStore.MediaColumns.DISPLAY_NAME
      )

    override fun createFromParcel(parcel: Parcel): MediaModel {
      return MediaModel(parcel)
    }

    override fun newArray(size: Int): Array<MediaModel?> {
      return arrayOfNulls(size)
    }
  }
}
