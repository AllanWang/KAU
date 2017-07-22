package ca.allanwang.kau.utils

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.IIcon

/**
 * Created by Allan Wang on 2017-05-29.
 */
@KauUtils fun IIcon.toDrawable(c: Context, sizeDp: Int = 24, @ColorInt color: Int = Color.WHITE, builder: IconicsDrawable.() -> Unit = {}): Drawable {
    val state = ColorStateList.valueOf(color)
    val icon = IconicsDrawable(c).icon(this).sizeDp(sizeDp).color(state)
    icon.builder()
    return icon
}