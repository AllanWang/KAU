package ca.allanwang.kau.utils

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.support.annotation.ColorInt
import android.support.annotation.StringRes
import android.support.annotation.TransitionRes
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputEditText
import android.support.transition.AutoTransition
import android.support.transition.Transition
import android.support.transition.TransitionInflater
import android.support.transition.TransitionManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import ca.allanwang.kau.ui.createSimpleRippleDrawable
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.IIcon


/**
 * Created by Allan Wang on 2017-05-31.
 */

@KauUtils inline fun <T : View> T.visible(): T {
    visibility = View.VISIBLE
    return this
}

@KauUtils inline fun <T : View> T.invisible(): T {
    visibility = View.INVISIBLE
    return this
}

@KauUtils inline fun <T : View> T.gone(): T {
    visibility = View.GONE
    return this
}

@KauUtils inline fun <T : View> T.invisibleIf(invisible: Boolean): T = if (invisible) invisible() else visible()

@KauUtils inline fun <T : View> T.visibleIf(visible: Boolean): T = if (visible) visible() else gone()

@KauUtils inline fun <T : View> T.goneIf(gone: Boolean): T = visibleIf(!gone)

@KauUtils inline val View.isVisible: Boolean get() = visibility == View.VISIBLE

@KauUtils inline val View.isInvisible: Boolean get() = visibility == View.INVISIBLE

@KauUtils inline val View.isGone: Boolean get() = visibility == View.GONE

fun View.snackbar(text: String, duration: Int = Snackbar.LENGTH_LONG, builder: Snackbar.() -> Unit = {}): Snackbar {
    val snackbar = Snackbar.make(this, text, duration)
    snackbar.builder()
    snackbar.show()
    return snackbar
}

fun View.snackbar(@StringRes textId: Int, duration: Int = Snackbar.LENGTH_LONG, builder: Snackbar.() -> Unit = {})
        = snackbar(context.string(textId), duration, builder)

@KauUtils fun TextView.setTextIfValid(@StringRes id: Int) {
    if (id > 0) text = context.string(id)
}

@KauUtils fun ImageView.setIcon(icon: IIcon?, sizeDp: Int = 24, @ColorInt color: Int = Color.WHITE, builder: IconicsDrawable.() -> Unit = {}) {
    if (icon == null) return
    setImageDrawable(icon.toDrawable(context, sizeDp = sizeDp, color = color, builder = builder))
}

@KauUtils inline val FloatingActionButton.isHidden get() = !isShown

fun FloatingActionButton.showIf(show: Boolean) = if (show) show() else hide()

fun FloatingActionButton.hideIf(hide: Boolean) = if (hide) hide() else show()

@KauUtils fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false): View = LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)

@KauUtils fun View.updateLeftMargin(margin: Int) = updateMargins(margin, KAU_LEFT)

@KauUtils fun View.updateTopMargin(margin: Int) = updateMargins(margin, KAU_TOP)

@KauUtils fun View.updateRightMargin(margin: Int) = updateMargins(margin, KAU_RIGHT)

@KauUtils fun View.updateBottomMargin(margin: Int) = updateMargins(margin, KAU_BOTTOM)

@KauUtils private fun View.updateMargins(margin: Int, flag: Int) {
    val p = (layoutParams as? ViewGroup.MarginLayoutParams) ?: return
    p.setMargins(
            if (flag == KAU_LEFT) margin else p.leftMargin,
            if (flag == KAU_TOP) margin else p.topMargin,
            if (flag == KAU_RIGHT) margin else p.rightMargin,
            if (flag == KAU_BOTTOM) margin else p.bottomMargin
    )
    requestLayout()
}

@KauUtils fun View.hideKeyboard() {
    clearFocus()
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(windowToken, 0)
}

@KauUtils fun View.showKeyboard() {
    requestFocus()
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

@KauUtils fun ViewGroup.transitionAuto(builder: AutoTransition.() -> Unit = {}) {
    val transition = AutoTransition()
    transition.builder()
    TransitionManager.beginDelayedTransition(this, transition)
}

@KauUtils fun ViewGroup.transitionDelayed(@TransitionRes id: Int, builder: Transition.() -> Unit = {}) {
    val transition = TransitionInflater.from(context).inflateTransition(id)
    transition.builder()
    TransitionManager.beginDelayedTransition(this, transition)
}

@KauUtils fun View.setRippleBackground(@ColorInt foregroundColor: Int, @ColorInt backgroundColor: Int) {
    background = createSimpleRippleDrawable(foregroundColor, backgroundColor)
}

@KauUtils val View.parentViewGroup: ViewGroup get() = parent as ViewGroup

val EditText.value: String get() = text.toString().trim()

val TextInputEditText.value: String get() = text.toString().trim()

/**
 * Generates a recycler view with match parent and a linearlayoutmanager, since it's so commonly used
 */
fun Context.fullLinearRecycler(rvAdapter: RecyclerView.Adapter<*>? = null, configs: RecyclerView.() -> Unit = {}): RecyclerView {
    return RecyclerView(this).apply {
        layoutManager = LinearLayoutManager(this@fullLinearRecycler)
        layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT)
        if (rvAdapter != null) adapter = rvAdapter
        configs()
    }
}