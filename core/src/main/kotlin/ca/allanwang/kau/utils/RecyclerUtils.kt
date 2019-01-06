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
package ca.allanwang.kau.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Allan Wang on 2017-07-11.
 */
fun RecyclerView.withMarginDecoration(sizeDp: Int, edgeFlags: Int) {
    addItemDecoration(MarginItemDecoration(sizeDp, edgeFlags))
}

class MarginItemDecoration(sizeDp: Int, val edgeFlags: Int) : RecyclerView.ItemDecoration() {

    private val sizePx = sizeDp.dpToPx

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        if (edgeFlags and KAU_LEFT > 0) outRect.left += sizePx
        if (edgeFlags and KAU_TOP > 0) outRect.top += sizePx
        if (edgeFlags and KAU_RIGHT > 0) outRect.right += sizePx
        if (edgeFlags and KAU_BOTTOM > 0) outRect.bottom += sizePx
    }
}
