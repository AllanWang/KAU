package ca.allanwang.kau.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.support.annotation.*
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.content.ContextCompat
import android.util.TypedValue
import android.widget.Toast
import ca.allanwang.kau.R
import com.afollestad.materialdialogs.MaterialDialog
import com.pitchedapps.frost.utils.ChangelogAdapter
import com.pitchedapps.frost.utils.parse
import java.util.*

/**
 * Created by Allan Wang on 2017-06-03.
 */
fun Context.startActivity(clazz: Class<out Activity>, clearStack: Boolean = false, intentBuilder: Intent.() -> Unit = {}, bundle: Bundle? = null) {
    val intent = (Intent(this, clazz))
    if (clearStack) intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
    intent.intentBuilder()
    ContextCompat.startActivity(this, intent, bundle)
    if (this is Activity && clearStack) finish()
}

/**
 * Bring in activity from the right
 */
fun Context.startActivitySlideIn(clazz: Class<out Activity>, clearStack: Boolean = false, intentBuilder: Intent.() -> Unit = {}, bundleBuilder: Bundle.() -> Unit = {}) {
    val bundle = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.kau_slide_in_right, R.anim.kau_fade_out).toBundle()
    bundle.bundleBuilder()
    startActivity(clazz, clearStack, intentBuilder, bundle)
}

/**
 * Bring in activity from behind while pushing the current activity to the right
 * This replicates the exit animation of a sliding activity, but is a forward creation
 * For the animation to work, the previous activity should not be in the stack (otherwise you wouldn't need this in the first place)
 * Consequently, the stack will be cleared by default
 */
fun Context.startActivitySlideOut(clazz: Class<out Activity>, clearStack: Boolean = true, intentBuilder: Intent.() -> Unit = {}, bundleBuilder: Bundle.() -> Unit = {}) {
    val bundle = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.kau_fade_in, R.anim.kau_slide_out_right_top).toBundle()
    bundle.bundleBuilder()
    startActivity(clazz, clearStack, intentBuilder, bundle)
}

//Toast helpers
fun Context.toast(@StringRes id: Int, duration: Int = Toast.LENGTH_LONG) = toast(this.string(id), duration)

fun Context.toast(text: String, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, text, duration).show()
}

//Resource retrievers
fun Context.string(@StringRes id: Int): String = getString(id)

fun Context.string(@StringRes id: Int, fallback: String?): String? = if (id > 0) string(id) else fallback
fun Context.string(holder: StringHolder?): String? = holder?.getString(this)
fun Context.color(@ColorRes id: Int): Int = ContextCompat.getColor(this, id)
fun Context.integer(@IntegerRes id: Int): Int = resources.getInteger(id)
fun Context.dimen(@DimenRes id: Int): Float = resources.getDimension(id)
fun Context.drawable(@DrawableRes id: Int): Drawable = ContextCompat.getDrawable(this, id)

//Attr retrievers
fun Context.resolveColor(@AttrRes attr: Int, fallback: Int = 0): Int {
    val a = theme.obtainStyledAttributes(intArrayOf(attr))
    try {
        return a.getColor(0, fallback)
    } finally {
        a.recycle()
    }
}

fun Context.resolveDrawable(@AttrRes attr: Int): Drawable? {
    val a = theme.obtainStyledAttributes(intArrayOf(attr))
    try {
        return a.getDrawable(0)
    } finally {
        a.recycle()
    }
}

fun Context.resolveBoolean(@AttrRes attr: Int, fallback: Boolean = false): Boolean {
    val a = theme.obtainStyledAttributes(intArrayOf(attr))
    try {
        return a.getBoolean(0, fallback)
    } finally {
        a.recycle()
    }
}

fun Context.resolveString(@AttrRes attr: Int, fallback: String = ""): String {
    val v = TypedValue()
    return if (theme.resolveAttribute(attr, v, true)) v.string.toString() else fallback
}

fun Context.showChangelog(@XmlRes xmlRes: Int, customize: MaterialDialog.Builder.() -> Unit = {}) {
    val mHandler = Handler()
    Thread(Runnable {
        val items = parse(this, xmlRes)
        mHandler.post(object : TimerTask() {
            override fun run() {
                val builder = MaterialDialog.Builder(this@showChangelog)
                        .title(R.string.kau_changelog)
                        .positiveText(R.string.kau_great)
                        .adapter(ChangelogAdapter(items), null)
                builder.customize()
                builder.show()
            }
        })
    }).start()
}

/**
 * Wrapper function for the MaterialDialog adapterBuilder
 * There is no need to call build() or show() as those are done by default
 */
fun Context.materialDialog(action: MaterialDialog.Builder.() -> Unit): MaterialDialog {
    val builder = MaterialDialog.Builder(this)
    builder.action()
    return builder.show()
}

val Context.isNetworkAvailable: Boolean
    get() {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting
    }

fun Context.getDip(value: Float): Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, resources.displayMetrics)