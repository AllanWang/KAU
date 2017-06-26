package ca.allanwang.kau.searchview

import android.content.Context
import android.graphics.Rect
import android.support.v7.widget.CardView
import android.util.AttributeSet
import android.view.ViewGroup

/**
 * Created by Allan Wang on 2017-06-26.
 *
 * CardView with a limited height
 * Leaves space for users to tap to exit and ensures that all search items are visible
 */
class SearchCard @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    val parentVisibleHeight: Int
        get() {
            val r = Rect()
            (parent as ViewGroup).getWindowVisibleDisplayFrame(r)
            return r.height()
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val trueHeightMeasureSpec = MeasureSpec.makeMeasureSpec((parentVisibleHeight * 0.9f).toInt(), MeasureSpec.AT_MOST)
        super.onMeasure(widthMeasureSpec, trueHeightMeasureSpec)
    }

}