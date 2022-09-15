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
package ca.allanwang.kau.about

import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import ca.allanwang.kau.ui.views.CollapsibleView
import ca.allanwang.kau.ui.views.CollapsibleViewDelegate

/** Created by Allan Wang on 2017-08-02. */
class CollapsibleTextView
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
  AppCompatTextView(context, attrs, defStyleAttr), CollapsibleView by CollapsibleViewDelegate() {

  init {
    initCollapsible(this)
  }

  override fun onConfigurationChanged(newConfig: Configuration?) {
    resetCollapsibleAnimation()
    super.onConfigurationChanged(newConfig)
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    val result = getCollapsibleDimension()
    setMeasuredDimension(result.first, result.second)
  }
}
