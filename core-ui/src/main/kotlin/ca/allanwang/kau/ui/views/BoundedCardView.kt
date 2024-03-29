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
import android.graphics.Rect
import android.util.AttributeSet
import androidx.cardview.widget.CardView
import ca.allanwang.kau.ui.R
import ca.allanwang.kau.utils.parentViewGroup

/**
 * Created by Allan Wang on 2017-06-26.
 *
 * CardView with a limited height This view should be used with wrap_content as its height Defaults
 * to at most the parent's visible height
 */
class BoundedCardView
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
  CardView(context, attrs, defStyleAttr) {

  /**
   * Maximum height possible, defined in dp (will be converted to px) Defaults to parent's visible
   * height
   */
  var maxHeight: Int = -1
  /** Percentage of resulting max height to fill Negative value = fill all of maxHeight */
  var maxHeightPercent: Float = -1.0f

  private val parentFrame = Rect()

  init {
    if (attrs != null) {
      val styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.BoundedCardView)
      maxHeight = styledAttrs.getDimensionPixelSize(R.styleable.BoundedCardView_maxHeight, -1)
      maxHeightPercent = styledAttrs.getFloat(R.styleable.BoundedCardView_maxHeightPercent, -1.0f)
      styledAttrs.recycle()
    }
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    parentViewGroup.getWindowVisibleDisplayFrame(parentFrame)
    var maxMeasureHeight = if (maxHeight > 0) maxHeight else parentFrame.height()
    if (maxHeightPercent > 0f) maxMeasureHeight = (maxMeasureHeight * maxHeightPercent).toInt()
    val trueHeightMeasureSpec = MeasureSpec.makeMeasureSpec(maxMeasureHeight, MeasureSpec.AT_MOST)
    super.onMeasure(widthMeasureSpec, trueHeightMeasureSpec)
  }
}
