package ca.allanwang.kau.utils

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by Allan Wang on 2017-07-11.
 */
fun RecyclerView.withMarginDecoration(sizeDp: Int, edgeFlags: Int) {
    addItemDecoration(MarginItemDecoration(sizeDp, edgeFlags))
}

class MarginItemDecoration(sizeDp: Int, val edgeFlags: Int) : RecyclerView.ItemDecoration() {

    val sizePx = sizeDp.dpToPx

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        if (edgeFlags and KAU_LEFT > 0) outRect.left += sizePx
        if (edgeFlags and KAU_TOP > 0) outRect.top += sizePx
        if (edgeFlags and KAU_RIGHT > 0) outRect.right += sizePx
        if (edgeFlags and KAU_BOTTOM > 0) outRect.bottom += sizePx
    }
}