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
import ca.allanwang.kau.utils.KAU_BOTTOM
import ca.allanwang.kau.utils.KAU_LEFT
import ca.allanwang.kau.utils.KAU_RIGHT
import ca.allanwang.kau.utils.KAU_TOP

/** Created by Allan Wang on 2017-07-11. */
class SlideAnimatorAdd(
    val fromEdge: Int,
    val slideFactor: Float = 1f,
    override var itemDelayFactor: Float = 0.125f
) : KauAnimatorAdd {

  override fun animationPrepare(holder: RecyclerView.ViewHolder): View.() -> Unit = {
    when (fromEdge) {
      KAU_TOP -> translationY = slideFactor * -height
      KAU_LEFT -> translationX = slideFactor * -width
      KAU_BOTTOM -> translationY = slideFactor * height
      KAU_RIGHT -> translationX = slideFactor * width
      else ->
          throw KauAnimatorException("Invalid edge flag used in Slide Animator; use one of KAU_*")
    }
    alpha = 0f
  }

  override fun animation(holder: RecyclerView.ViewHolder): ViewPropertyAnimator.() -> Unit = {
    translationY(0f)
    translationX(0f)
    alpha(1f)
  }

  override fun animationCleanup(holder: RecyclerView.ViewHolder): View.() -> Unit = {
    translationY = 0f
    translationX = 0f
    alpha = 1f
  }

  override fun getDelay(remove: Long, move: Long, change: Long): Long = 0L
}

class SlideAnimatorRemove(
    val fromEdge: Int,
    val slideFactor: Float = 1f,
    override var itemDelayFactor: Float = 0.125f
) : KauAnimatorRemove {
  override fun animation(holder: RecyclerView.ViewHolder): ViewPropertyAnimator.() -> Unit = {
    with(holder.itemView) {
      when (fromEdge) {
        KAU_TOP -> translationY(slideFactor * -height)
        KAU_LEFT -> translationX(slideFactor * -width)
        KAU_BOTTOM -> translationY(slideFactor * height)
        KAU_RIGHT -> translationX(slideFactor * width)
        else ->
            throw KauAnimatorException("Invalid edge flag used in Slide Animator; use one of KAU_*")
      }
    }
    alpha(0f)
  }

  override fun animationCleanup(holder: RecyclerView.ViewHolder): View.() -> Unit = {
    translationY = 0f
    translationX = 0f
    alpha = 1f
  }

  override fun getDelay(remove: Long, move: Long, change: Long): Long = 0L
}
