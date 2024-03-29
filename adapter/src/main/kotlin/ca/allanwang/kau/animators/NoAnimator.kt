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

/** Created by Allan Wang on 2017-08-02. */
class NoAnimatorAdd(override var itemDelayFactor: Float = 0f) : KauAnimatorAdd {

  override fun animationPrepare(holder: RecyclerView.ViewHolder): View.() -> Unit = {}

  override fun animation(holder: RecyclerView.ViewHolder): ViewPropertyAnimator.() -> Unit = {}

  override fun animationCleanup(holder: RecyclerView.ViewHolder): View.() -> Unit = {}

  override fun getDelay(remove: Long, move: Long, change: Long): Long = 0L
}

class NoAnimatorRemove(override var itemDelayFactor: Float = 0f) : KauAnimatorRemove {

  override fun animation(holder: RecyclerView.ViewHolder): ViewPropertyAnimator.() -> Unit = {}

  override fun animationCleanup(holder: RecyclerView.ViewHolder): View.() -> Unit = {}

  override fun getDelay(remove: Long, move: Long, change: Long): Long = 0L
}

object NoAnimatorChange : KauAnimatorChange {

  override fun changeOldAnimation(
    holder: RecyclerView.ViewHolder,
    changeInfo: BaseItemAnimator.ChangeInfo
  ): ViewPropertyAnimator.() -> Unit = {}

  override fun changeNewAnimation(
    holder: RecyclerView.ViewHolder
  ): ViewPropertyAnimator.() -> Unit = {}

  override fun changeAnimationCleanup(holder: RecyclerView.ViewHolder): View.() -> Unit = {
    alpha = 1f
  }
}
