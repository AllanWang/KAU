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
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import ca.allanwang.kau.mediapicker.databinding.KauBlurredImageviewBinding
import ca.allanwang.kau.ui.views.MeasureSpecContract
import ca.allanwang.kau.ui.views.MeasureSpecDelegate
import ca.allanwang.kau.utils.scaleXY
import ca.allanwang.kau.utils.setBackgroundColorRes
import ca.allanwang.kau.utils.setIcon
import ca.allanwang.kau.utils.visible
import com.mikepenz.iconics.typeface.library.googlematerial.GoogleMaterial
import jp.wasabeef.blurry.internal.BlurFactor
import jp.wasabeef.blurry.internal.BlurTask

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

    var isBlurred = false
        private set

    val imageBase: ImageView get() = binding.imageBase
    
    private val binding: KauBlurredImageviewBinding = KauBlurredImageviewBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        initAttrs(context, attrs)
        binding.imageForeground.setIcon(GoogleMaterial.Icon.gmd_check, 30)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val result = onMeasure(this, widthMeasureSpec, heightMeasureSpec)
        super.onMeasure(result.first, result.second)
    }

    override fun clearAnimation() {
        super.clearAnimation()
        with (binding) {
            imageBase.clearAnimation()
            imageBlur.clearAnimation()
            imageForeground.clearAnimation()
        }
    }

    private fun View.scaleAnimate(scale: Float) =
        animate().scaleXY(scale).setDuration(ANIMATION_DURATION)

    private fun View.alphaAnimate(alpha: Float) =
        animate().alpha(alpha).setDuration(ANIMATION_DURATION)

    /**
     * Applies a blur and fills the blur image asynchronously
     * When ready, scales the image down and shows the blur & foreground
     */
    fun blur() {
        if (isBlurred) return
        isBlurred = true
        val factor = BlurFactor()
        factor.width = width
        factor.height = height
        BlurTask(imageBase, factor) {
            with (binding) {
                imageBlur.setImageDrawable(it)
                scaleAnimate(ANIMATION_SCALE).start()
                imageBlur.alphaAnimate(1f).start()
                imageForeground.alphaAnimate(1f).start()
            }
        }.execute()
    }

    /**
     * Clears animations and blurs the image without further animations
     * This method is relatively instantaneous, as retrieving the blurred image
     * is still asynchronous and takes time
     */
    fun blurInstantly() {
        isBlurred = true
        clearAnimation()
        val factor = BlurFactor()
        factor.width = width
        factor.height = height
        BlurTask(imageBase, factor) { drawable ->
            with (binding) {
                imageBlur.setImageDrawable(drawable)
                scaleXY = ANIMATION_SCALE
                imageBlur.alpha = 1f
                imageForeground.alpha = 1f
            }
        }.execute()
    }

    /**
     * Animate view back to original state and remove drawable when finished
     */
    fun removeBlur() {
        if (!isBlurred) return
        isBlurred = false
        scaleAnimate(1.0f).start()
        with (binding) {
            imageBlur.alphaAnimate(0f).withEndAction { imageBlur.setImageDrawable(null) }.start()
            imageForeground.alphaAnimate(0f).start()
        }
    }

    /**
     * Clear all animations and unblur the image
     */
    fun removeBlurInstantly() {
        isBlurred = false
        clearAnimation()
        scaleX = 1.0f
        scaleX = 1.0f
        with (binding) {
            imageBlur.alpha = 0f
            imageBlur.setImageDrawable(null)
            imageForeground.alpha = 0f
        }
    }

    /**
     * Switch blur state and apply transition
     *
     * @return true if new state is blurred; false otherwise
     */
    fun toggleBlur(): Boolean {
        if (isBlurred) removeBlur()
        else blur()
        return isBlurred
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
        with(binding) {
            fullAction { it.visible().background = null }
            imageForeground.setBackgroundColorRes(R.color.kau_blurred_image_selection_overlay)
            imageForeground.setIcon(GoogleMaterial.Icon.gmd_check, 30, Color.WHITE)
        }
    }

    private fun KauBlurredImageviewBinding.fullAction(action: (View) -> Unit) {
        action(this@BlurredImageView)
        action(imageBase)
        action(imageBlur)
        action(imageForeground)
    }
}
