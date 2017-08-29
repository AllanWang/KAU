package ca.allanwang.kau.mediapicker

import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager

/**
 * Created by Allan Wang on 29/08/2017.
 */
interface GlideContract {

    fun glide(v: View): RequestManager

}

class GlideDelegate : GlideContract {
    override fun glide(v: View) = ((v.context as? MediaPickerCore<*>)?.glide ?: Glide.with(v))!!
}