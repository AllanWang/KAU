package ca.allanwang.kau.imagepicker

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import ca.allanwang.kau.ui.views.MeasureSpecContract
import ca.allanwang.kau.ui.views.MeasureSpecDelegate
import ca.allanwang.kau.ui.views.MeasuredImageView
import ca.allanwang.kau.utils.bindView
import ca.allanwang.kau.utils.inflate
import ca.allanwang.kau.utils.setIcon
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import jp.wasabeef.blurry.internal.BlurFactor
import jp.wasabeef.blurry.internal.BlurTask

/**
 * Created by Allan Wang on 2017-07-14.
 *
 * ImageView that is can be blurred and selected
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
    val imageForeground: MeasuredImageView by bindView(R.id.image_foreground)

    init {
        inflate(R.layout.kau_blurrable_imageview, true)
        initAttrs(context, attrs)
        imageForeground.setIcon(GoogleMaterial.Icon.gmd_check, 30)
    }

    companion object {
        const val ANIMATION_DURATION = 200L
        const val ANIMATION_SCALE = 0.95f
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val result = onMeasure(this, widthMeasureSpec, heightMeasureSpec)
        super.onMeasure(result.first, result.second)
    }

    fun isBlurred(): Boolean {
        return blurred
    }

    fun blur() {
        if (blurred) return
        blurred = true
        val factor = BlurFactor()
        factor.width = width
        factor.height = height
        val task = BlurTask(imageBase, factor) {
            imageBlur.setImageDrawable(it)
            scaleAnimate(ANIMATION_SCALE).start()
            imageBlur.alphaAnimate(1f).start()
            imageForeground.alphaAnimate(1f).start()
        }
        task.execute()
    }

    private fun View.scaleAnimate(scale: Float) = animate().scaleX(scale).scaleY(scale).setDuration(ANIMATION_DURATION)
    private fun View.alphaAnimate(alpha: Float) = animate().alpha(alpha).setDuration(ANIMATION_DURATION)

    fun removeBlur() {
        if (!blurred) return
        blurred = false
        scaleAnimate(1.0f).start()
        imageBlur.alphaAnimate(0f).withEndAction { imageBlur.setImageDrawable(null) }.start()
        imageForeground.alphaAnimate(0f).start()
    }

    fun toggleBlur() {
        if (blurred) removeBlur()
        else blur()
    }
}