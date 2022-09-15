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
package ca.allanwang.kau.swipe

import kotlin.math.max
import kotlin.math.min

/**
 * Created by Mr.Jude on 2015/8/26.
 *
 * Updated by Allan Wang on 2017/07/05
 *
 * Helper class to give the previous activity an offset as the main page is pulled
 */
internal class RelativeSlider(var curPage: SwipeBackPage) : SwipeListener {

  var offset = 0f

  var enabled: Boolean
    get() = curPage.hasListener(this)
    set(value) {
      if (value) curPage.addListener(this) else curPage.removeListener(this)
    }

  /** Set offset of previous page based on the edge flag and percentage scrolled */
  override fun onScroll(percent: Float, px: Int, edgeFlag: Int) {
    if (offset == 0f) return // relative slider is not enabled
    val page = SwipeBackHelper.getPrePage(curPage) ?: return
    if (percent == 0f) {
      page.swipeBackLayout.x = 0f
      page.swipeBackLayout.y = 0f
      return
    }
    when (edgeFlag) {
      SWIPE_EDGE_LEFT ->
        page.swipeBackLayout.x = min(-offset * max(1 - percent, 0f) + DEFAULT_OFFSET, 0f)
      SWIPE_EDGE_RIGHT ->
        page.swipeBackLayout.x = min(offset * max(1 - percent, 0f) - DEFAULT_OFFSET, 0f)
      SWIPE_EDGE_TOP ->
        page.swipeBackLayout.y = min(-offset * max(1 - percent, 0f) + DEFAULT_OFFSET, 0f)
      SWIPE_EDGE_BOTTOM ->
        page.swipeBackLayout.y = min(offset * max(1 - percent, 0f) - DEFAULT_OFFSET, 0f)
    }
  }

  override fun onEdgeTouch() {}

  /** Reset offsets for previous page */
  override fun onScrollToClose(edgeFlag: Int) {
    val prePage = SwipeBackHelper.getPrePage(curPage) ?: return
    prePage.swipeBackLayout.x = 0f
    prePage.swipeBackLayout.y = 0f
  }

  companion object {
    private const val DEFAULT_OFFSET = 40
  }
}
