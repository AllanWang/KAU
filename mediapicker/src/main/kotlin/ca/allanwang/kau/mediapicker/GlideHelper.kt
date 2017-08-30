package ca.allanwang.kau.mediapicker

import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager

/**
 * Created by Allan Wang on 29/08/2017.
 *
 * Basic helper to fetch the [RequestManager] from the activity if it exists, before creating another one
 */
internal interface GlideContract {
    fun glide(v: View): RequestManager
}

internal class GlideDelegate : GlideContract {
    override fun glide(v: View) = ((v.context as? MediaPickerCore<*>)?.glide ?: Glide.with(v))!!
}