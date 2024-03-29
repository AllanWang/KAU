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

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import ca.allanwang.kau.logging.KL
import java.lang.ref.WeakReference

/**
 * Created by Mr.Jude on 2015/8/3.
 *
 * Updated by Allan Wang on 2017/07/05
 */
internal class SwipeBackPage(activity: Activity) :
  SwipeBackContractInternal by SwipeBackLayout(activity) {

  var activityRef = WeakReference(activity)
  var slider: RelativeSlider

  /** initializing is the equivalent to onCreate since that is when the page is created */
  init {
    activity.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    activity.window.decorView.setBackgroundColor(Color.TRANSPARENT)
    swipeBackLayout.layoutParams =
      ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
      )
    slider = RelativeSlider(this)
  }

  fun onPostCreate() {
    handleLayout()
  }

  override var swipeEnabled: Boolean
    get() = swipeBackLayout.swipeEnabled
    set(value) {
      swipeBackLayout.swipeEnabled = value
      handleLayout()
    }

  private fun handleLayout() {
    val activity =
      activityRef.get() ?: return KL.v { "KauSwipe activity ref gone during handleLayout" }
    if (swipeEnabled) swipeBackLayout.attachToActivity(activity)
    else swipeBackLayout.removeFromActivity(activity)
  }

  fun setClosePercent(percent: Float): SwipeBackPage {
    swipeBackLayout.scrollThreshold = percent
    return this
  }
}

internal interface SwipeBackContractInternal : SwipeBackContract {
  val swipeBackLayout: SwipeBackLayout
}

interface SwipeBackContract {
  /** Toggle main touch intercept Disabling will remove the swipe back handler */
  var swipeEnabled: Boolean
  /**
   * Set the background color for the outside of the page This dynamically fades as the page gets
   * closer to exiting
   */
  var scrimColor: Int
  var edgeSize: Int
  /** Set the flag for which edge the page is scrolling from */
  var edgeFlag: Int
  /** Set the scrolling threshold for wish a page is deemed closing */
  var scrollThreshold: Float
  /**
   * Toggle main touch intercept Disabling will just ignore touch events, but the handler will
   * remain attached
   */
  var disallowIntercept: Boolean
  /** Set the min velocity of the view drag helper */
  var minVelocity: Float
  /** Set the max velocity of the view drag helper */
  var maxVelocity: Float
  /** Set the sensitivity of the view drag helper */
  var sensitivity: Float
  /** Dynamically change the alpha for the status bar and nav bar as the page scrolls */
  var transitionSystemBars: Boolean

  /** Sets edge size based on screen size */
  fun setEdgeSizePercent(swipeEdgePercent: Float)

  fun addListener(listener: SwipeListener)
  fun removeListener(listener: SwipeListener)
  fun hasListener(listener: SwipeListener): Boolean
  fun scrollToFinishActivity()
}
