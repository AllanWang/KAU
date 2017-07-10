package ca.allanwang.kau.utils

import android.content.Context
import android.graphics.Color
import android.graphics.Outline
import android.graphics.Rect
import android.support.annotation.ColorInt
import android.support.annotation.StringRes
import android.support.annotation.TransitionRes
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.transition.AutoTransition
import android.support.transition.Transition
import android.support.transition.TransitionInflater
import android.support.transition.TransitionManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import ca.allanwang.kau.logging.KL
import ca.allanwang.kau.ui.createSimpleRippleDrawable
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.IIcon


/**
 * Created by Allan Wang on 2017-05-31.
 */
@KauUtils fun <T : View> T.visible(): T {
    visibility = View.VISIBLE
    return this
}

@KauUtils fun <T : View> T.invisible(): T {
    visibility = View.INVISIBLE
    return this
}

@KauUtils fun <T : View> T.gone(): T {
    visibility = View.GONE
    return this
}

fun <T : View> T.invisibleIf(invisible:Boolean):T =
        if (invisible) invisible() else visible()

fun <T : View> T.visibleIf(visible:Boolean):T = if (visible) visible() else gone()

fun <T : View> T.goneIf(gone:Boolean):T = visibleIf(!gone)

@KauUtils fun View.isVisible(): Boolean = visibility == View.VISIBLE
@KauUtils fun View.isInvisible(): Boolean = visibility == View.INVISIBLE
@KauUtils fun View.isGone(): Boolean = visibility == View.GONE

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

val FloatingActionButton.isHidden
    get() = !isShown

fun FloatingActionButton.showIf(show:Boolean) = if (show) show() else hide()

fun FloatingActionButton.hideIf(hide:Boolean) = if (hide) hide() else show()

fun ViewGroup.inflate(layoutId:Int, attachToRoot:Boolean = false):View =
        LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)

fun View.updateLeftMargin(left:Int = -1) = updateMargins(left)

fun View.updateTopMargin(top:Int = -1) = updateMargins(-1, top)

fun View.updateRightMargin(right:Int = -1) = updateMargins(-1, -1, right)

fun View.updateBottomMargin(bottom:Int = -1) = updateMargins(-1, -1, -1, bottom)

fun View.updateMargins(left:Int = -1, top:Int = -1, right:Int = -1, bottom:Int = -1) {
    if (layoutParams is ViewGroup.MarginLayoutParams) {
        val p = layoutParams as ViewGroup.MarginLayoutParams
        val newLeft = if (left >= 0) left else p.leftMargin
        val newTop = if (top >= 0) top else p.topMargin
        val newRight = if (right >= 0) right else p.rightMargin
        val newBottom = if (bottom >= 0) bottom else p.bottomMargin
        p.setMargins(newLeft, newTop, newRight, newBottom)
        requestLayout()
    }
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

@KauUtils val View.parentViewGroup: ViewGroup
    get() = parent as ViewGroup

@KauUtils val View.parentVisibleHeight: Int
    get() {
        val r = Rect()
        parentViewGroup.getWindowVisibleDisplayFrame(r)
        return r.height()
    }

val CIRCULAR_OUTLINE: ViewOutlineProvider = object : ViewOutlineProvider() {
    override fun getOutline(view: View, outline: Outline) {
        KL.d("CIRCULAR OUTLINE")
        outline.setOval(view.paddingLeft,
                view.paddingTop,
                view.width - view.paddingRight,
                view.height - view.paddingBottom)
    }
}

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