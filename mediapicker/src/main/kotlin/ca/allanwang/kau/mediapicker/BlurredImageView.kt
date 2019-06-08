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
package ca.allanwang.kau.mediapicker

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import ca.allanwang.kau.ui.views.MeasureSpecContract
import ca.allanwang.kau.ui.views.MeasureSpecDelegate
import ca.allanwang.kau.utils.inflate
import ca.allanwang.kau.utils.scaleXY
import ca.allanwang.kau.utils.setBackgroundColorRes
import ca.allanwang.kau.utils.setIcon
import ca.allanwang.kau.utils.visible
import com.mikepenz.iconics.typeface.library.googlematerial.GoogleMaterial
import jp.wasabeef.blurry.internal.BlurFactor
import jp.wasabeef.blurry.internal.BlurTask
import kotlinx.android.synthetic.main.kau_blurred_imageview.view.*

/**
 * Created by Allan Wang on 2017-07-14.
 *
 * ImageView that can be blurred and selected
 * The frame is composed of three layers: the base, the blur, and the foreground
 * Images should be placed in the base view, and the blur view should not be touched
 * as the class will handle it
 * The foreground by default contains a white checkmark, but can be customized or hidden depending on the situation
 */
class BlurredImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), MeasureSpecContract by MeasureSpecDelegate() {

    private var blurred = false
    val imageBase: ImageView get() = image_base

    init {
        inflate(R.layout.kau_blurred_imageview, true)
        initAttrs(context, attrs)
        image_foreground.setIcon(GoogleMaterial.Icon.gmd_check, 30)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val result = onMeasure(this, widthMeasureSpec, heightMeasureSpec)
        super.onMeasure(result.first, result.second)
    }

    override fun clearAnimation() {
        super.clearAnimation()
        imageBase.clearAnimation()
        image_blur.clearAnimation()
        image_foreground.clearAnimation()
    }

    private fun View.scaleAnimate(scale: Float) = animate().scaleXY(scale).setDuration(ANIMATION_DURATION)
    private fun View.alphaAnimate(alpha: Float) = animate().alpha(alpha).setDuration(ANIMATION_DURATION)

    fun isBlurred(): Boolean {
        return blurred
    }

    /**
     * Applies a blur and fills the blur image asynchronously
     * When ready, scales the image down and shows the blur & foreground
     */
    fun blur() {
        if (blurred) return
        blurred = true
        val factor = BlurFactor()
        factor.width = width
        factor.height = height
        BlurTask(imageBase, factor) {
            image_blur.setImageDrawable(it)
            scaleAnimate(ANIMATION_SCALE).start()
            image_blur.alphaAnimate(1f).start()
            image_foreground.alphaAnimate(1f).start()
        }.execute()
    }

    /**
     * Clears animations and blurs the image without further animations
     * This method is relatively instantaneous, as retrieving the blurred image
     * is still asynchronous and takes time
     */
    fun blurInstantly() {
        blurred = true
        clearAnimation()
        val factor = BlurFactor()
        factor.width = width
        factor.height = height
        BlurTask(imageBase, factor) { drawable ->
            image_blur.setImageDrawable(drawable)
            scaleXY = ANIMATION_SCALE
            image_blur.alpha = 1f
            image_foreground.alpha = 1f
        }.execute()
    }

    /**
     * Animate view back to original state and remove drawable when finished
     */
    fun removeBlur() {
        if (!blurred) return
        blurred = false
        scaleAnimate(1.0f).start()
        image_blur.alphaAnimate(0f).withEndAction { image_blur.setImageDrawable(null) }.start()
        image_foreground.alphaAnimate(0f).start()
    }

    /**
     * Clear all animations and unblur the image
     */
    fun removeBlurInstantly() {
        blurred = false
        clearAnimation()
        scaleX = 1.0f
        scaleX = 1.0f
        image_blur.alpha = 0f
        image_blur.setImageDrawable(null)
        image_foreground.alpha = 0f
    }

    /**
     * Switch blur state and apply transition
     *
     * @return true if new state is blurred; false otherwise
     */
    fun toggleBlur(): Boolean {
        if (blurred) removeBlur()
        else blur()
        return blurred
    }

    /**
     * Clears all of the blur effects to restore the original states
     * If views were modified in other ways, this method won't affect it
     */
    fun reset() {
        removeBlurInstantly()
        imageBase.setImageDrawable(null)
    }

    /**
     * Reset most of possible changes to the view
     */
    fun fullReset() {
        reset()
        fullAction({ it.visible().background = null })
        image_foreground.setBackgroundColorRes(R.color.kau_blurred_image_selection_overlay)
        image_foreground.setIcon(GoogleMaterial.Icon.gmd_check, 30, Color.WHITE)
    }

    private fun fullAction(action: (View) -> Unit) {
        action(this)
        action(imageBase)
        action(image_blur)
        action(image_foreground)
    }
}
