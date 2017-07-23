package ca.allanwang.kau.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.support.annotation.*
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.content.ContextCompat
import android.util.TypedValue
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import ca.allanwang.kau.R
import ca.allanwang.kau.logging.KL
import com.afollestad.materialdialogs.MaterialDialog


/**
 * Created by Allan Wang on 2017-06-03.
 */
@SuppressLint("NewApi")
fun Context.startActivity(
        clazz: Class<out Activity>,
        clearStack: Boolean = false,
        transition: Boolean = false,
        bundle: Bundle? = null,
        intentBuilder: Intent.() -> Unit = {}) {
    val intent = (Intent(this, clazz))
    if (clearStack) intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
    intent.intentBuilder()
    val fullBundle = if (transition && this is Activity && buildIsLollipopAndUp)
        ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
    else Bundle()
    if (transition && this !is Activity) KL.d("Cannot make scene transition when context is not an instance of an Activity")
    if (bundle != null) fullBundle.putAll(bundle)
    ContextCompat.startActivity(this, intent, if (fullBundle.isEmpty) null else fullBundle)
    if (this is Activity && clearStack) finish()
}

/**
 * Bring in activity from the right
 */
fun Context.startActivitySlideIn(clazz: Class<out Activity>, clearStack: Boolean = false, intentBuilder: Intent.() -> Unit = {}, bundleBuilder: Bundle.() -> Unit = {}) {
    val bundle = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.kau_slide_in_right, R.anim.kau_fade_out).toBundle()
    bundle.bundleBuilder()
    startActivity(clazz, clearStack, intentBuilder = intentBuilder, bundle = bundle)
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
    startActivity(clazz, clearStack, intentBuilder = intentBuilder, bundle = bundle)
}

fun Context.startPlayStoreLink(@StringRes packageIdRes: Int) = startPlayStoreLink(string(packageIdRes))

fun Context.startPlayStoreLink(packageId: String) {
    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageId")))
}

/**
 * Starts a url
 * If given a series of links, will open the first one that isn't null
 */
fun Context.startLink(vararg url: String?) {
    val link = url.firstOrNull { !it.isNullOrBlank() } ?: return
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
    startActivity(browserIntent)
}

//Toast helpers
fun Context.toast(@StringRes id: Int, duration: Int = Toast.LENGTH_LONG) = toast(this.string(id), duration)

fun Context.toast(text: String, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, text, duration).show()
}

//Resource retrievers
inline fun Context.string(@StringRes id: Int): String = getString(id)

inline fun Context.string(@StringRes id: Int, fallback: String?): String? = if (id > 0) string(id) else fallback
inline fun Context.color(@ColorRes id: Int): Int = ContextCompat.getColor(this, id)
inline fun Context.integer(@IntegerRes id: Int): Int = resources.getInteger(id)
inline fun Context.dimen(@DimenRes id: Int): Float = resources.getDimension(id)
inline fun Context.dimenPixelSize(@DimenRes id: Int): Int = resources.getDimensionPixelSize(id)
inline fun Context.drawable(@DrawableRes id: Int): Drawable = ContextCompat.getDrawable(this, id)
inline fun Context.drawable(@DrawableRes id: Int, fallback: Drawable?): Drawable? = if (id > 0) drawable(id) else fallback
inline fun Context.interpolator(@InterpolatorRes id: Int) = AnimationUtils.loadInterpolator(this, id)
inline fun Context.animation(@AnimRes id: Int) = AnimationUtils.loadAnimation(this, id)
/**
 * Returns plural form of res. The quantity is also passed to the formatter as an int
 */
inline fun Context.plural(@PluralsRes id: Int, quantity: Number)
        = resources.getQuantityString(id, quantity.toInt(), quantity.toInt())

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

/**
 * Wrapper function for the MaterialDialog adapterBuilder
 * There is no need to call build() or show() as those are done by default
 */
inline fun Context.materialDialog(action: MaterialDialog.Builder.() -> Unit): MaterialDialog {
    val builder = MaterialDialog.Builder(this)
    builder.action()
    return builder.show()
}

fun Context.getDip(value: Float): Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, resources.displayMetrics)

inline val Context.isRtl: Boolean
    get() = resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL

/**
 * Determine if the navigation bar will be on the bottom of the screen, based on logic in
 * PhoneWindowManager.
 */
inline val Context.isNavBarOnBottom: Boolean
    get() {
        val cfg = resources.configuration
        val dm = resources.displayMetrics
        val canMove = dm.widthPixels != dm.heightPixels && cfg.smallestScreenWidthDp < 600
        return !canMove || dm.widthPixels < dm.heightPixels
    }

fun Context.hasPermission(permissions: String) = !buildIsMarshmallowAndUp || ContextCompat.checkSelfPermission(this, permissions) == PackageManager.PERMISSION_GRANTED

fun Context.copyToClipboard(text: String?, label: String = "Copied Text", showToast: Boolean = true) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.primaryClip = ClipData.newPlainText(label, text ?: "")
    if (showToast) toast(R.string.kau_text_copied)
}

fun Context.shareText(text: String?) {
    if (text == null) return toast(R.string.kau_text_is_null)
    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "text/plain"
    intent.putExtra(Intent.EXTRA_TEXT, text)
    startActivity(Intent.createChooser(intent, string(R.string.kau_share)))
}