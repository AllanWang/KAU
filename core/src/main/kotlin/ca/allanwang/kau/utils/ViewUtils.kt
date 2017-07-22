@file:Suppress("NOTHING_TO_INLINE")

package ca.allanwang.kau.utils

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
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

@KauUtils inline fun View.setBackgroundColorRes(@ColorRes color: Int) = setBackgroundColor(context.color(color))

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

/**
 * Set left margin to a value in px
 */
@KauUtils fun View.setMarginLeft(margin: Int) = setMargins(margin, KAU_LEFT)

/**
 * Set top margin to a value in px
 */
@KauUtils fun View.setMarginTop(margin: Int) = setMargins(margin, KAU_TOP)

/**
 * Set right margin to a value in px
 */
@KauUtils fun View.setMarginRight(margin: Int) = setMargins(margin, KAU_RIGHT)

/**
 * Set bottom margin to a value in px
 */
@KauUtils fun View.setMarginBottom(margin: Int) = setMargins(margin, KAU_BOTTOM)

/**
 * Set left and right margins to a value in px
 */
@KauUtils fun View.setMarginHorizontal(margin: Int) = setMargins(margin, KAU_HORIZONTAL)

/**
 * Set top and bottom margins to a value in px
 */
@KauUtils fun View.setMarginVertical(margin: Int) = setMargins(margin, KAU_VERTICAL)

/**
 * Set all margins to a value in px
 */
@KauUtils fun View.setMargin(margin: Int) = setMargins(margin, KAU_ALL)

/**
 * Base margin setter
 * returns true if setting is successful, false otherwise
 */
@KauUtils private fun View.setMargins(margin: Int, flag: Int): Boolean {
    val p = (layoutParams as? ViewGroup.MarginLayoutParams) ?: return false
    p.setMargins(
            if (flag and KAU_LEFT > 0) margin else p.leftMargin,
            if (flag and KAU_TOP > 0) margin else p.topMargin,
            if (flag and KAU_RIGHT > 0) margin else p.rightMargin,
            if (flag and KAU_BOTTOM > 0) margin else p.bottomMargin
    )
    requestLayout()
    return true
}

/**
 * Set left padding to a value in px
 */
@KauUtils fun View.setPaddingLeft(padding: Int) = setPadding(padding, KAU_LEFT)

/**
 * Set top padding to a value in px
 */
@KauUtils fun View.setPaddingTop(padding: Int) = setPadding(padding, KAU_TOP)

/**
 * Set right padding to a value in px
 */
@KauUtils fun View.setPaddingRight(padding: Int) = setPadding(padding, KAU_RIGHT)

/**
 * Set bottom padding to a value in px
 */
@KauUtils fun View.setPaddingBottom(padding: Int) = setPadding(padding, KAU_BOTTOM)

/**
 * Set left and right padding to a value in px
 */
@KauUtils fun View.setPaddingHorizontal(padding: Int) = setPadding(padding, KAU_HORIZONTAL)

/**
 * Set top and bottom padding to a value in px
 */
@KauUtils fun View.setPaddingVertical(padding: Int) = setPadding(padding, KAU_VERTICAL)

/**
 * Set all padding to a value in px
 */
@KauUtils fun View.setPadding(padding: Int) = setPadding(padding, KAU_ALL)

/**
 * Base padding setter
 */
@KauUtils private fun View.setPadding(padding: Int, flag: Int) {
    setPadding(
            if (flag and KAU_LEFT > 0) padding else paddingLeft,
            if (flag and KAU_TOP > 0) padding else paddingTop,
            if (flag and KAU_RIGHT > 0) padding else paddingRight,
            if (flag and KAU_BOTTOM > 0) padding else paddingBottom
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

@KauUtils inline val View.parentViewGroup: ViewGroup get() = parent as ViewGroup

inline val EditText.value: String get() = text.toString().trim()

inline val TextInputEditText.value: String get() = text.toString().trim()

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

/**
 * Animate a transition for a FloatinActionButton
 * If it is not shown, the action will be invoked directly and the fab will be shown
 * If it is already shown, scaling and alpha animations will be added to the action
 */
inline fun FloatingActionButton.transition(crossinline action: FloatingActionButton.() -> Unit) {
    if (isHidden) {
        action()
        show()
    } else {
        var transitioned = false
        ValueAnimator.ofFloat(1.0f, 0.0f, 1.0f).apply {
            duration = 500L
            addUpdateListener {
                val x = it.animatedValue as Float
                val scale = x * 0.3f + 0.7f
                scaleX = scale
                scaleY = scale
                imageAlpha = (x * 255).toInt()
                if (it.animatedFraction > 0.5f && !transitioned) {
                    transitioned = true
                    action()
                }
            }
            start()
        }
    }
}

/**
 * Attaches a listener to the recyclerview to hide the fab when it is scrolling downwards
 * The fab will reappear when scrolling has stopped or if the user scrolls up
 */
fun FloatingActionButton.hideOnDownwardsScroll(recycler: RecyclerView) {
    recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (newState == android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE && !isShown) show();
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (dy > 0 && isShown) hide()
            else if (dy < 0 && isHidden) show()
        }
    })
}