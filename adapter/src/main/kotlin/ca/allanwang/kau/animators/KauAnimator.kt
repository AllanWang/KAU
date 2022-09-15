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

import android.view.ViewPropertyAnimator
import androidx.recyclerview.widget.RecyclerView
import ca.allanwang.kau.utils.KAU_BOTTOM
import ca.allanwang.kau.utils.KAU_RIGHT
import kotlin.math.max

/** Created by Allan Wang on 2017-06-27. */
open class KauAnimator(
    val addAnimator: KauAnimatorAdd = SlideAnimatorAdd(KAU_BOTTOM),
    val removeAnimator: KauAnimatorRemove = SlideAnimatorRemove(KAU_RIGHT),
    val changeAnimator: KauAnimatorChange = FadeAnimatorChange()
) : BaseItemAnimator() {

  open fun startDelay(holder: RecyclerView.ViewHolder, duration: Long, factor: Float) =
      max(0L, (holder.adapterPosition * duration * factor).toLong())

  override fun removeAnimation(holder: RecyclerView.ViewHolder): ViewPropertyAnimator {
    return holder.itemView.animate().apply {
      startDelay = startDelay(holder, removeDuration, removeAnimator.itemDelayFactor)
      duration = removeDuration
      interpolator = this@KauAnimator.interpolator
      removeAnimator.animation(holder)()
    }
  }

  override fun removeAnimationCleanup(holder: RecyclerView.ViewHolder) {
    holder.itemView.apply { removeAnimator.animationCleanup(holder)() }
  }

  override fun getRemoveDelay(remove: Long, move: Long, change: Long): Long =
      removeAnimator.getDelay(remove, move, change)

  override fun addAnimationPrepare(holder: RecyclerView.ViewHolder) {
    holder.itemView.apply { addAnimator.animationPrepare(holder)() }
  }

  override fun addAnimation(holder: RecyclerView.ViewHolder): ViewPropertyAnimator {
    return holder.itemView.animate().apply {
      startDelay = startDelay(holder, addDuration, addAnimator.itemDelayFactor)
      duration = addDuration
      interpolator = this@KauAnimator.interpolator
      addAnimator.animation(holder)()
    }
  }

  override fun addAnimationCleanup(holder: RecyclerView.ViewHolder) {
    holder.itemView.apply { addAnimator.animationCleanup(holder)() }
  }

  override fun getAddDelay(remove: Long, move: Long, change: Long): Long =
      addAnimator.getDelay(remove, move, change)

  override fun changeOldAnimation(
      holder: RecyclerView.ViewHolder,
      changeInfo: ChangeInfo
  ): ViewPropertyAnimator {
    return holder.itemView.animate().apply {
      duration = changeDuration
      interpolator = this@KauAnimator.interpolator
      changeAnimator.changeOldAnimation(holder, changeInfo)()
    }
  }

  override fun changeNewAnimation(holder: RecyclerView.ViewHolder): ViewPropertyAnimator {
    return holder.itemView.animate().apply {
      duration = changeDuration
      interpolator = this@KauAnimator.interpolator
      changeAnimator.changeNewAnimation(holder)()
    }
  }

  override fun changeAnimationCleanup(holder: RecyclerView.ViewHolder) {
    holder.itemView.apply { changeAnimator.changeAnimationCleanup(holder)() }
  }
}
