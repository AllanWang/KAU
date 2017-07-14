package ca.allanwang.kau.ui.views

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView

/**
 * Created by Allan Wang on 2017-07-14.
 */
class MeasuredImageView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0
) : ImageView(context, attrs, defStyleAttr, defStyleRes), MeasureSpecContract by MeasureSpecDelegate() {

    init {
        initAttrs(context, attrs)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val result = onMeasure(this, widthMeasureSpec, heightMeasureSpec)
        super.onMeasure(result.first, result.second)
    }

}