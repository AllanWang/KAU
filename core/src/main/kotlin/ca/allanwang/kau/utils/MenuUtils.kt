package ca.allanwang.kau.utils

import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.view.Menu

fun Menu.changeOptionVisibility(id:Int, visible:Boolean) {
    findItem(id)?.isVisible = visible
}

fun Menu.setItemTitle(id:Int, title:String) {
    findItem(id)?.title = title
}

fun Menu.setOptionIcon(id:Int, @DrawableRes iconRes:Int) = findItem(id)?.setIcon(iconRes)

fun Menu.setOptionIcon(id:Int, iconRes:Drawable) {
    findItem(id)?.icon = iconRes
}