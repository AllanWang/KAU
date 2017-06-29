package ca.allanwang.kau.views

import android.content.Context
import android.graphics.Rect
import android.support.v7.widget.CardView
import android.util.AttributeSet
import ca.allanwang.kau.R
import ca.allanwang.kau.utils.parentViewGroup
import ca.allanwang.kau.utils.parentVisibleHeight


/**
 * Created by Allan Wang on 2017-06-26.
 *
 * CardView with a limited height
 * This view should be used with wrap_content as its height
 * Defaults to at most the parent's visible height
 */
class KauBoundedCardView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    /**
     * Maximum height possible, defined in dp (will be converted to px)
     * Defaults to parent's visible height
     */
    var maxHeight: Int = -1
    /**
     * Percentage of resulting max height to fill
     * Negative value = fill all of maxHeight
     */
    var maxHeightPercent: Float = -1.0f

    init {
        if (attrs != null) {
            val styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.KauBoundedCardView)
            maxHeight = styledAttrs.getDimensionPixelSize(R.styleable.KauBoundedCardView_kau_maxHeight, -1)
            maxHeightPercent = styledAttrs.getFloat(R.styleable.KauBoundedCardView_kau_maxHeightPercent, -1.0f)
            styledAttrs.recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var maxMeasureHeight = if (maxHeight > 0) maxHeight else parentVisibleHeight
        if (maxHeightPercent > 0f) maxMeasureHeight = (maxMeasureHeight * maxHeightPercent).toInt()
        val trueHeightMeasureSpec = MeasureSpec.makeMeasureSpec(maxMeasureHeight, MeasureSpec.AT_MOST)
        super.onMeasure(widthMeasureSpec, trueHeightMeasureSpec)
    }

}