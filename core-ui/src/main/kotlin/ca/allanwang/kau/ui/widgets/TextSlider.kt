/*
 * Copyright 2018 Allan Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ca.allanwang.kau.ui.widgets

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextSwitcher
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import ca.allanwang.kau.kotlin.lazyUi
import ca.allanwang.kau.ui.R
import java.util.EmptyStackException
import java.util.Stack

/**
 * Created by Allan Wang on 2017-06-21.
 *
 * Text switcher with global text color and embedded sliding animations
 * Also has a stack to keep track of title changes
 */
class TextSlider @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : TextSwitcher(context, attrs) {

    val titleStack: Stack<CharSequence?> = Stack()

    /**
     * Holds a mapping of animation types to their respective animations
     */
    val animationMap = mapOf(
        ANIMATION_NONE to null,
        ANIMATION_SLIDE_HORIZONTAL to AnimationBundle(
            R.anim.kau_slide_in_right, R.anim.kau_slide_out_left,
            R.anim.kau_slide_in_left, R.anim.kau_slide_out_right
        ),
        ANIMATION_SLIDE_VERTICAL to AnimationBundle(
            R.anim.kau_slide_in_bottom, R.anim.kau_slide_out_top,
            R.anim.kau_slide_in_top, R.anim.kau_slide_out_bottom
        )
    )

    /**
     * Holds lazy instances of the animations
     */
    inner class AnimationBundle(
        private val nextIn: Int,
        private val nextOut: Int,
        private val prevIn: Int,
        private val prevOut: Int
    ) {
        val NEXT_IN: Animation by lazyUi { AnimationUtils.loadAnimation(context, nextIn) }
        val NEXT_OUT: Animation by lazyUi { AnimationUtils.loadAnimation(context, nextOut) }
        val PREV_IN: Animation by lazyUi { AnimationUtils.loadAnimation(context, prevIn) }
        val PREV_OUT: Animation by lazyUi { AnimationUtils.loadAnimation(context, prevOut) }
    }

    companion object {
        const val ANIMATION_NONE = 1000
        const val ANIMATION_SLIDE_HORIZONTAL = 1001
        const val ANIMATION_SLIDE_VERTICAL = 1002
    }

    var animationType: Int = ANIMATION_SLIDE_HORIZONTAL

    var textColor: Int = Color.WHITE
        get() = field
        set(value) {
            field = value
            (getChildAt(0) as TextView).setTextColor(value)
            (getChildAt(1) as TextView).setTextColor(value)
        }
    val isRoot: Boolean
        get() = titleStack.size <= 1

    init {
        if (attrs != null) {
            val styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.TextSlider)
            animationType = styledAttrs.getInteger(R.styleable.TextSlider_animation_type, ANIMATION_SLIDE_HORIZONTAL)
            styledAttrs.recycle()
        }
    }

    override fun setText(text: CharSequence?) {
        if ((currentView as TextView).text == text) return
        super.setText(text)
    }

    override fun setCurrentText(text: CharSequence?) {
        if (titleStack.isNotEmpty()) titleStack.pop()
        titleStack.push(text)
        super.setCurrentText(text)
    }

    fun setNextText(text: CharSequence?) {
        if (titleStack.isEmpty()) {
            setCurrentText(text)
            return
        }
        titleStack.push(text)
        val anim = animationMap[animationType]
        inAnimation = anim?.NEXT_IN
        outAnimation = anim?.NEXT_OUT
        setText(text)
    }

    /**
     * Sets the text as the previous title
     * No further checks are done, so be sure to verify with [isRoot]
     */
    @Throws(EmptyStackException::class)
    fun setPrevText() {
        titleStack.pop()
        val anim = animationMap[animationType]
        inAnimation = anim?.PREV_IN
        outAnimation = anim?.PREV_OUT
        val text = titleStack.peek()
        setText(text)
    }

    init {
        setFactory {
            TextView(context).apply {
                // replica of toolbar title
                gravity = Gravity.START
                setSingleLine()
                ellipsize = TextUtils.TruncateAt.END
                TextViewCompat.setTextAppearance(this, R.style.TextAppearance_AppCompat_Title)
            }
        }
    }
}
