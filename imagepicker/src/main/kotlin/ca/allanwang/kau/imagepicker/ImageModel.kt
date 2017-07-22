package ca.allanwang.kau.imagepicker

import android.database.Cursor
import android.os.Parcel
import android.os.Parcelable
import android.provider.MediaStore
import android.support.annotation.NonNull


/**
 * Created by Allan Wang on 2017-07-14.
 */

data class ImageModel(val size: Long, val dateModified: Long, val data: String, val displayName: String) : Parcelable {

    constructor(@NonNull cursor: Cursor) : this(
            cursor.getLong(MediaStore.Images.Media.SIZE),
            cursor.getLong(MediaStore.Images.Media.DATE_MODIFIED),
            cursor.getString(MediaStore.Images.Media.DATA),
            cursor.getString(MediaStore.Images.Media.DISPLAY_NAME)
    )

    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readLong(),
            parcel.readString(),
            parcel.readString())


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(this.size)
        parcel.writeLong(this.dateModified)
        parcel.writeString(this.data)
        parcel.writeString(this.displayName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ImageModel> {
        override fun createFromParcel(parcel: Parcel): ImageModel {
            return ImageModel(parcel)
        }

        override fun newArray(size: Int): Array<ImageModel?> {
            return arrayOfNulls(size)
        }
    }

}

private fun Cursor.getString(name: String) = getString(getColumnIndex(name))
private fun Cursor.getLong(name: String) = getLong(getColumnIndex(name))