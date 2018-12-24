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

/**
 * Created by Allan Wang on 2017-06-27.
 */
open class DefaultAnimator : BaseItemAnimator() {

    override fun removeAnimation(holder: RecyclerView.ViewHolder): ViewPropertyAnimator {
        return holder.itemView.animate().apply {
            alpha(0f)
            duration = this@DefaultAnimator.removeDuration
            interpolator = this@DefaultAnimator.interpolator
        }
    }

    override fun removeAnimationCleanup(holder: RecyclerView.ViewHolder) {
        holder.itemView.alpha = 1f
    }

    override fun addAnimationPrepare(holder: RecyclerView.ViewHolder) {
        holder.itemView.alpha = 0f
    }

    override fun addAnimation(holder: RecyclerView.ViewHolder): ViewPropertyAnimator {
        return holder.itemView.animate().apply {
            alpha(1f)
            duration = this@DefaultAnimator.addDuration
            interpolator = this@DefaultAnimator.interpolator
        }
    }

    override fun addAnimationCleanup(holder: RecyclerView.ViewHolder) {
        holder.itemView.alpha = 1f
    }

    override fun changeOldAnimation(holder: RecyclerView.ViewHolder, changeInfo: ChangeInfo): ViewPropertyAnimator {
        return holder.itemView.animate().apply {
            alpha(0f)
            translationX(changeInfo.toX.toFloat() - changeInfo.fromX)
            translationY(changeInfo.toY.toFloat() - changeInfo.fromY)
            duration = this@DefaultAnimator.changeDuration
            interpolator = this@DefaultAnimator.interpolator
        }
    }

    override fun changeNewAnimation(holder: RecyclerView.ViewHolder): ViewPropertyAnimator {
        return holder.itemView.animate().apply {
            alpha(1f)
            translationX(0f)
            translationY(0f)
            duration = this@DefaultAnimator.changeDuration
            interpolator = this@DefaultAnimator.interpolator
        }
    }

    override fun changeAnimationCleanup(holder: RecyclerView.ViewHolder) {
        holder.itemView.alpha = 1f
    }
}
