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
fun Activity.restart(extras: ((Intent) -> Unit)? = null) {
    val i = Intent(this, this::class.java)
    i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
    extras?.invoke(i)
    startActivity(i)
    overridePendingTransition(0, 0) //No transitions
    finish()
    overridePendingTransition(0, 0)
}

fun Context.startActivity(clazz: Class<out Activity>, clearStack: Boolean = false, intentBuilder: Intent.() -> Unit = {}, bundle: Bundle? = null) {
    val intent = (Intent(this, clazz))
    if (clearStack) intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
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

var Activity.navigationBarColor: Int
    get() = if (buildIsLollipopAndUp) window.navigationBarColor else Color.BLACK
    set(value) {
        if (buildIsLollipopAndUp) window.navigationBarColor = value
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

fun Context.showChangelog(@XmlRes xmlRes: Int) {
    val mHandler = Handler()
    Thread(Runnable {
        val items = parse(this, xmlRes)
        mHandler.post(object : TimerTask() {
            override fun run() {
                MaterialDialog.Builder(this@showChangelog)
                        .title(R.string.kau_changelog)
                        .positiveText(R.string.kau_great)
                        .adapter(ChangelogAdapter(items), null)
                        .show()
            }
        })
    }).start()
}

val Context.isNetworkAvailable: Boolean
    get() {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting
    }

fun Context.getDip(value: Float): Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, resources.displayMetrics)