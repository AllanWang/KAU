/*
 * Copyright 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ca.allanwang.kau.views

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import ca.allanwang.kau.R
import ca.allanwang.kau.logging.KL
import ca.allanwang.kau.utils.dimenPixelSize
import ca.allanwang.kau.utils.getFont
import ca.allanwang.kau.utils.parentVisibleHeight

/**
 * A view which punches out some text from an opaque color block, allowing you to see through it.
 */
class KauCutoutTextView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val textPaint: TextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private var cutout: Bitmap? = null
    var foregroundColor = Color.MAGENTA
    var text: String? = "Text"
    var overlayType: Int = 0 //todo add vector overlay options
    private var textSize: Float = 0f
    private var textY: Float = 0f
    private var textX: Float = 0f
    private var heightPercentage: Float = 0f
    private var minHeight: Float = 0f
    private val maxTextSize: Float

    init {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.KauCutoutTextView, 0, 0)
            if (a.hasValue(R.styleable.KauCutoutTextView_kau_font))
                textPaint.typeface = context.getFont(a.getString(R.styleable.KauCutoutTextView_kau_font))
            foregroundColor = a.getColor(R.styleable.KauCutoutTextView_kau_foregroundColor, foregroundColor)
            text = a.getString(R.styleable.KauCutoutTextView_android_text) ?: text
            minHeight = a.getDimension(R.styleable.KauCutoutTextView_android_minHeight, minHeight)
            heightPercentage = a.getFloat(R.styleable.KauCutoutTextView_kau_heightPercentageToScreen, heightPercentage)
            a.recycle()
        }
        maxTextSize = context.dimenPixelSize(R.dimen.kau_display_4_text_size).toFloat()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        calculateTextPosition()
        createBitmap()
        KL.d("Size changed")
    }

    private fun calculateTextPosition() {
        val targetWidth = width / PHI
        textSize = getSingleLineTextSize(text!!, textPaint, targetWidth, 0f, maxTextSize,
                0.5f, resources.displayMetrics)
        textPaint.textSize = textSize

        // measuring text is fun :] see: https://chris.banes.me/2014/03/27/measuring-text/
        textX = (width - textPaint.measureText(text)) / 2
        val textBounds = Rect()
        textPaint.getTextBounds(text, 0, text!!.length, textBounds)
        val textHeight = textBounds.height().toFloat()
        textY = (height + textHeight) / 2
    }

    /**
     * If height percent is specified, ensure it is met
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minHeight = Math.max(minHeight, heightPercentage * parentVisibleHeight)
        val trueHeightMeasureSpec = if (minHeight > 0)
            MeasureSpec.makeMeasureSpec(Math.max(minHeight.toInt(), measuredHeight), MeasureSpec.EXACTLY)
        else heightMeasureSpec
        super.onMeasure(widthMeasureSpec, trueHeightMeasureSpec)
    }

    /**
     * Recursive binary search to find the best size for the text.

     * Adapted from https://github.com/grantland/android-autofittextview
     */
    fun getSingleLineTextSize(text: String,
                              paint: TextPaint,
                              targetWidth: Float,
                              low: Float,
                              high: Float,
                              precision: Float,
                              metrics: DisplayMetrics): Float {
        val mid = (low + high) / 2.0f

        paint.textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, mid, metrics)
        val maxLineWidth = paint.measureText(text)

        if (high - low < precision) {
            return low
        } else if (maxLineWidth > targetWidth) {
            return getSingleLineTextSize(text, paint, targetWidth, low, mid, precision, metrics)
        } else if (maxLineWidth < targetWidth) {
            return getSingleLineTextSize(text, paint, targetWidth, mid, high, precision, metrics)
        } else {
            return mid
        }
    }

    private fun createBitmap() {
        if (!(cutout?.isRecycled ?: true))
            cutout?.recycle()
        if (width == 0 || height == 0) return
        cutout = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        cutout!!.setHasAlpha(true)
        val cutoutCanvas = Canvas(cutout!!)
        cutoutCanvas.drawColor(foregroundColor)

        // this is the magic – Clear mode punches out the bitmap
        textPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        cutoutCanvas.drawText(text, textX, textY, textPaint)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawBitmap(cutout!!, 0f, 0f, null)
    }

    override fun hasOverlappingRendering(): Boolean = true

    companion object {
        val PHI = 1.6182f
    }
}
