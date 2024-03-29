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
package ca.allanwang.kau.ui.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import ca.allanwang.kau.ui.R
import ca.allanwang.kau.utils.dimenPixelSize
import ca.allanwang.kau.utils.getFont
import ca.allanwang.kau.utils.parentViewGroup
import ca.allanwang.kau.utils.toBitmap
import kotlin.math.max
import kotlin.math.min

/**
 * A view which punches out some text from an opaque color block, allowing you to see through it.
 *
 * Inspired by <a href="https://github.com/nickbutcher/plaid">Plaid</a>
 */
class CutoutView
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
  View(context, attrs, defStyleAttr) {

  companion object {
    const val PHI = 1.6182f
    const val TYPE_EMPTY = 100
    const val TYPE_TEXT = 101
    const val TYPE_DRAWABLE = 102
  }

  private val paint: TextPaint =
    TextPaint().apply {
      isAntiAlias = true
      xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }
  private var bitmapScaling: Float = 1f
  private var cutout: Bitmap? = null
  var foregroundColor = Color.MAGENTA
  var text: String? = "Text"
    set(value) {
      field = value
      cutoutType =
        when {
          value != null -> TYPE_TEXT
          drawable != null -> TYPE_DRAWABLE
          else -> TYPE_EMPTY
        }
    }
  var cutoutType: Int = TYPE_EMPTY
  private var textSize: Float = 0f
  private var cutoutY: Float = 0f
  private var cutoutX: Float = 0f
  var drawable: Drawable? = null
    set(value) {
      field = value
      cutoutType =
        when {
          drawable != null -> TYPE_DRAWABLE
          value != null -> TYPE_TEXT
          else -> TYPE_EMPTY
        }
    }
  private var heightPercentage: Float = 0f
  private var minHeight: Float = 0f
  private val maxTextSize: Float
  private val parentFrame = Rect()

  init {
    if (attrs != null) {
      val a = context.obtainStyledAttributes(attrs, R.styleable.CutoutView, 0, 0)
      if (a.hasValue(R.styleable.CutoutView_font))
        paint.typeface = context.getFont(a.getString(R.styleable.CutoutView_font)!!)
      foregroundColor = a.getColor(R.styleable.CutoutView_foregroundColor, foregroundColor)
      text = a.getString(R.styleable.CutoutView_android_text) ?: text
      minHeight = a.getDimension(R.styleable.CutoutView_android_minHeight, minHeight)
      heightPercentage =
        a.getFloat(R.styleable.CutoutView_heightPercentageToScreen, heightPercentage)
      a.recycle()
    }
    maxTextSize = context.dimenPixelSize(R.dimen.kau_display_4_text_size).toFloat()
  }

  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    super.onSizeChanged(w, h, oldw, oldh)
    calculatePosition()
    createBitmap()
  }

  private fun calculatePosition() {
    when (cutoutType) {
      TYPE_TEXT -> calculateTextPosition()
      TYPE_DRAWABLE -> calculateImagePosition()
    }
  }

  private fun calculateTextPosition() {
    val targetWidth = width / PHI
    textSize =
      getSingleLineTextSize(
        text!!,
        paint,
        targetWidth,
        0f,
        maxTextSize,
        0.5f,
        resources.displayMetrics
      )
    paint.textSize = textSize

    // measuring text is fun :] see: https://chris.banes.me/2014/03/27/measuring-text/
    cutoutX = (width - paint.measureText(text)) / 2
    val textBounds = Rect()
    paint.getTextBounds(text, 0, text!!.length, textBounds)
    val textHeight = textBounds.height().toFloat()
    cutoutY = (height + textHeight) / 2
  }

  private fun calculateImagePosition() {
    if (drawable!!.intrinsicHeight <= 0 || drawable!!.intrinsicWidth <= 0)
      throw IllegalArgumentException("Drawable's intrinsic size cannot be less than 0")
    val targetWidth = width / PHI
    val targetHeight = height / PHI
    bitmapScaling =
      min(targetHeight / drawable!!.intrinsicHeight, targetWidth / drawable!!.intrinsicWidth)
    cutoutX = (width - drawable!!.intrinsicWidth * bitmapScaling) / 2
    cutoutY = (height - drawable!!.intrinsicHeight * bitmapScaling) / 2
  }

  /** If height percent is specified, ensure it is met */
  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    parentViewGroup.getWindowVisibleDisplayFrame(parentFrame)
    val minHeight = max(minHeight, heightPercentage * parentFrame.height())
    val trueHeightMeasureSpec =
      if (minHeight > 0)
        MeasureSpec.makeMeasureSpec(max(minHeight.toInt(), measuredHeight), MeasureSpec.EXACTLY)
      else heightMeasureSpec
    super.onMeasure(widthMeasureSpec, trueHeightMeasureSpec)
  }

  /**
   * Recursive binary search to find the best size for the text.
   *
   * Adapted from https://github.com/grantland/android-autofittextview
   */
  fun getSingleLineTextSize(
    text: String,
    paint: TextPaint,
    targetWidth: Float,
    low: Float,
    high: Float,
    precision: Float,
    metrics: DisplayMetrics
  ): Float {
    val mid = (low + high) / 2.0f

    paint.textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, mid, metrics)
    val maxLineWidth = paint.measureText(text)

    return when {
      high - low < precision -> low
      maxLineWidth > targetWidth ->
        getSingleLineTextSize(text, paint, targetWidth, low, mid, precision, metrics)
      maxLineWidth < targetWidth ->
        getSingleLineTextSize(text, paint, targetWidth, mid, high, precision, metrics)
      else -> mid
    }
  }

  private fun createBitmap() {
    if (cutout?.isRecycled == false) cutout?.recycle()
    if (width == 0 || height == 0) return
    cutout = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply { setHasAlpha(true) }
    val cutoutCanvas = Canvas(cutout!!)
    cutoutCanvas.drawColor(foregroundColor)

    when (cutoutType) {
      TYPE_TEXT -> {
        cutoutCanvas.drawText(text!!, cutoutX, cutoutY, paint)
      }
      TYPE_DRAWABLE -> {
        cutoutCanvas.drawBitmap(
          drawable!!.toBitmap(bitmapScaling, Bitmap.Config.ALPHA_8),
          cutoutX,
          cutoutY,
          paint
        )
      }
      TYPE_EMPTY -> {
        // do nothing
      }
    }
  }

  override fun onDraw(canvas: Canvas) {
    canvas.drawBitmap(cutout!!, 0f, 0f, null)
  }

  override fun hasOverlappingRendering(): Boolean = true
}
