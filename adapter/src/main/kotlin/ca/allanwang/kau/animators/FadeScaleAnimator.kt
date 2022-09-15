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
package ca.allanwang.kau.animators

import android.view.View
import android.view.ViewPropertyAnimator
import androidx.recyclerview.widget.RecyclerView
import ca.allanwang.kau.utils.scaleXY

/** Created by Allan Wang on 2017-07-11. */
class FadeScaleAnimatorAdd(
    val scaleFactor: Float = 1.0f,
    override var itemDelayFactor: Float = 0.125f
) : KauAnimatorAdd {

  override fun animationPrepare(holder: RecyclerView.ViewHolder): View.() -> Unit = {
    scaleXY = scaleFactor
    alpha = 0f
  }

  override fun animation(holder: RecyclerView.ViewHolder): ViewPropertyAnimator.() -> Unit = {
    scaleXY(1f)
    alpha(1f)
  }

  override fun animationCleanup(holder: RecyclerView.ViewHolder): View.() -> Unit = {
    scaleXY = 1f
    alpha = 1f
  }

  override fun getDelay(remove: Long, move: Long, change: Long): Long = 0L
}

class FadeScaleAnimatorRemove(
    val scaleFactor: Float = 1.0f,
    override var itemDelayFactor: Float = 0.125f
) : KauAnimatorRemove {

  override fun animation(holder: RecyclerView.ViewHolder): ViewPropertyAnimator.() -> Unit = {
    scaleXY(scaleFactor)
    alpha(0f)
  }

  override fun animationCleanup(holder: RecyclerView.ViewHolder): View.() -> Unit = {
    scaleXY = 1f
    alpha = 1f
  }

  override fun getDelay(remove: Long, move: Long, change: Long): Long = 0L
}

class FadeAnimatorChange : KauAnimatorChange {

  override fun changeOldAnimation(
      holder: RecyclerView.ViewHolder,
      changeInfo: BaseItemAnimator.ChangeInfo
  ): ViewPropertyAnimator.() -> Unit = { alpha(0f) }

  override fun changeNewAnimation(
      holder: RecyclerView.ViewHolder
  ): ViewPropertyAnimator.() -> Unit = { alpha(1f) }

  override fun changeAnimationCleanup(holder: RecyclerView.ViewHolder): View.() -> Unit = {
    alpha = 1f
  }
}
