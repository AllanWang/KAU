package ca.allanwang.kau.views

import android.content.Context
import android.graphics.Color
import android.support.annotation.AnimRes
import android.support.v4.widget.TextViewCompat
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextSwitcher
import android.widget.TextView
import ca.allanwang.kau.R
import java.util.*

/**
 * Created by Allan Wang on 2017-06-21.
 *
 * Text switcher with global text color and embedded sliding animations
 * Also has a stack to keep track of title changes
 */
class TextSlider @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null
) : TextSwitcher(context, attrs) {

    val titleStack: Stack<CharSequence?> = Stack()

    inner class Animations(
            private @param: AnimRes val nextIn: Int,
            private @param: AnimRes val nextOut: Int,
            private @param: AnimRes val prevIn: Int,
            private @param: AnimRes val prevOut: Int
    ) {

        val NEXT_IN: Animation by lazy { AnimationUtils.loadAnimation(context, R.anim.kau_slide_in_left) }
        val NEXT_OUT: Animation by lazy { AnimationUtils.loadAnimation(context, R.anim.kau_slide_in_right) }
        val PREV_IN: Animation by lazy { AnimationUtils.loadAnimation(context, R.anim.kau_slide_in_top) }
        val PREV_OUT: Animation by lazy { AnimationUtils.loadAnimation(context, R.anim.kau_slide_in_bottom) }

    }

    private val SLIDE_IN_LEFT: Animation by lazy { AnimationUtils.loadAnimation(context, R.anim.kau_slide_in_left) }
    private val SLIDE_IN_RIGHT: Animation by lazy { AnimationUtils.loadAnimation(context, R.anim.kau_slide_in_right) }
    private val SLIDE_IN_TOP: Animation by lazy { AnimationUtils.loadAnimation(context, R.anim.kau_slide_in_top) }
    private val SLIDE_IN_BOTTOM: Animation by lazy { AnimationUtils.loadAnimation(context, R.anim.kau_slide_in_bottom) }

    private val SLIDE_OUT_LEFT: Animation by lazy { AnimationUtils.loadAnimation(context, R.anim.kau_slide_out_left) }
    private val SLIDE_OUT_RIGHT: Animation by lazy { AnimationUtils.loadAnimation(context, R.anim.kau_slide_out_right) }
    private val SLIDE_OUT_TOP: Animation by lazy { AnimationUtils.loadAnimation(context, R.anim.kau_slide_out_top) }
    private val SLIDE_OUT_BOTTOM: Animation by lazy { AnimationUtils.loadAnimation(context, R.anim.kau_slide_out_bottom) }

    var animate: Boolean = true
    var horizontal: Boolean = true

    var textColor: Int = Color.WHITE
        get() = field
        set(value) {
            field = value
            (getChildAt(0) as TextView).setTextColor(value)
            (getChildAt(1) as TextView).setTextColor(value)
        }
    val isRoot: Boolean
        get() = titleStack.size <= 1

    override fun setText(text: CharSequence?) {
        if ((currentView as TextView).text == text) return
        super.setText(text)
    }

    fun setTextSlideUp(text: CharSequence?) {
        inAnimation = if (animate) SLIDE_IN_BOTTOM else null
        outAnimation = if (animate) SLIDE_OUT_TOP else null
        setText(text)
    }

    fun setTextSlideDown(text: CharSequence?) {
        inAnimation = if (animate) SLIDE_IN_TOP else null
        outAnimation = if (animate) SLIDE_OUT_BOTTOM else null
        setText(text)
    }

    fun setTextSlideLeft(text: CharSequence?) {
        inAnimation = if (animate) SLIDE_IN_RIGHT else null
        outAnimation = if (animate) SLIDE_OUT_LEFT else null
        setText(text)
    }

    fun setTextSlideRight(text: CharSequence?) {
        inAnimation = if (animate) SLIDE_IN_LEFT else null
        outAnimation = if (animate) SLIDE_OUT_RIGHT else null
        setText(text)
    }

    override fun setCurrentText(text: CharSequence?) {
        if (titleStack.isNotEmpty()) titleStack.pop()
        titleStack.push(text)
        super.setCurrentText(text)
    }

    fun setNextText(text: CharSequence?) {
        titleStack.push(text)
        if (horizontal) setTextSlideLeft(text) else setTextSlideUp(text)
    }

    /**
     * Sets the text as the previous title
     * No further checks are done, so be sure to verify with [isRoot]
     */
    @Throws(EmptyStackException::class)
    fun setPrevText() {
        titleStack.pop()
        val text = titleStack.peek()
        if (horizontal) setTextSlideRight(text) else setTextSlideDown(text)
    }

    init {
        setFactory {
            TextView(context).apply {
                //replica of toolbar title
                gravity = Gravity.START
                setSingleLine()
                ellipsize = TextUtils.TruncateAt.END
                TextViewCompat.setTextAppearance(this, R.style.TextAppearance_AppCompat)
            }
        }
    }
}