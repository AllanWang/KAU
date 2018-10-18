package ca.allanwang.kau.about

import android.content.Context
import android.content.res.Configuration
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import ca.allanwang.kau.ui.views.CollapsibleView
import ca.allanwang.kau.ui.views.CollapsibleViewDelegate

/**
 * Created by Allan Wang on 2017-08-02.
 *
 */
class CollapsibleTextView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr), CollapsibleView by CollapsibleViewDelegate() {

    init {
        initCollapsible(this)
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        resetCollapsibleAnimation()
        super.onConfigurationChanged(newConfig)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val result = getCollapsibleDimension()
        setMeasuredDimension(result.first, result.second)
    }
}