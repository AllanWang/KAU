package ca.allanwang.kau.utils

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.support.annotation.ColorInt
import android.view.Menu
import ca.allanwang.kau.R
import com.mikepenz.iconics.typeface.IIcon

/**
 * Created by Allan Wang on 2017-06-21.
 */

/**
 * Restarts an activity from itself without animations
 * Keeps its existing extra bundles and has a builder to accept other parameters
 */
fun Activity.restart(builder: Intent.() -> Unit = {}) {
    val i = Intent(this, this::class.java)
    i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
    i.putExtras(intent.extras)
    i.builder()
    startActivity(i)
    overridePendingTransition(0, 0) //No transitions
    finish()
    overridePendingTransition(0, 0)
}

fun Activity.finishSlideOut() {
    finish()
    overridePendingTransition(R.anim.kau_fade_in, R.anim.kau_slide_out_right_top)
}

var Activity.navigationBarColor: Int
    get() = if (buildIsLollipopAndUp) window.navigationBarColor else Color.BLACK
    set(value) {
        if (buildIsLollipopAndUp) window.navigationBarColor = value
    }

var Activity.statusBarColor: Int
    get() = if (buildIsLollipopAndUp) window.statusBarColor else Color.BLACK
    set(value) {
        if (buildIsLollipopAndUp) window.statusBarColor = value
    }

/**
 * Themes the base menu icons and adds iicons programmatically based on ids
 *
 * Call in [Activity.onCreateOptionsMenu]
 */
fun Activity.setMenuIcons(menu: Menu, @ColorInt color: Int = Color.WHITE, vararg iicons: Pair<Int, IIcon>) {
    iicons.forEach { (id, iicon) ->
        menu.findItem(id).icon = iicon.toDrawable(this, sizeDp = 20, color = color)
    }
}