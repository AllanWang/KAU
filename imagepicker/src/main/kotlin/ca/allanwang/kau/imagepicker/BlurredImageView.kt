package ca.allanwang.kau.imagepicker

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import ca.allanwang.kau.ui.views.MeasureSpecContract
import ca.allanwang.kau.ui.views.MeasureSpecDelegate
import ca.allanwang.kau.utils.*
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.mikepenz.iconics.IconicsDrawable
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
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes), MeasureSpecContract by MeasureSpecDelegate() {

    private var blurred = false
    val imageBase: ImageView by bindView(R.id.image_base)
    internal val imageBlur: ImageView by bindView(R.id.image_blur)
    val imageForeground: ImageView by bindView(R.id.image_foreground)

    init {
        inflate(R.layout.kau_blurred_imageview, true)
        initAttrs(context, attrs)
        imageForeground.setIcon(GoogleMaterial.Icon.gmd_check, 30)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val result = onMeasure(this, widthMeasureSpec, heightMeasureSpec)
        super.onMeasure(result.first, result.second)
    }

    override fun clearAnimation() {
        super.clearAnimation()
        imageBase.clearAnimation()
        imageBlur.clearAnimation()
        imageForeground.clearAnimation()
    }

    private fun View.scaleAnimate(scale: Float) = animate().scaleX(scale).scaleY(scale).setDuration(ANIMATION_DURATION)
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
            imageBlur.setImageDrawable(it)
            scaleAnimate(ANIMATION_SCALE).start()
            imageBlur.alphaAnimate(1f).start()
            imageForeground.alphaAnimate(1f).start()
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
            imageBlur.setImageDrawable(drawable)
            scaleX = ANIMATION_SCALE
            scaleY = ANIMATION_SCALE
            imageBlur.alpha = 1f
            imageForeground.alpha = 1f
        }.execute()
    }

    /**
     * Animate view back to original state and remove drawable when finished
     */
    fun removeBlur() {
        if (!blurred) return
        blurred = false
        scaleAnimate(1.0f).start()
        imageBlur.alphaAnimate(0f).withEndAction { imageBlur.setImageDrawable(null) }.start()
        imageForeground.alphaAnimate(0f).start()
    }


    /**
     * Clear all animations and unblur the image
     */
    fun removeBlurInstantly() {
        blurred = false
        clearAnimation()
        scaleX = 1.0f
        scaleX = 1.0f
        imageBlur.alpha = 0f
        imageBlur.setImageDrawable(null)
        imageForeground.alpha = 0f
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
        imageForeground.setBackgroundColorRes(R.color.kau_blurred_image_selection_overlay)
        imageForeground.setIcon(GoogleMaterial.Icon.gmd_check, 30, Color.WHITE)
    }

    private fun fullAction(action: (View) -> Unit) {
        action(this)
        action(imageBase)
        action(imageBlur)
        action(imageForeground)
    }
}