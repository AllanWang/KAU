package ca.allanwang.kau.ui.views

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import ca.allanwang.kau.R
import ca.allanwang.kau.utils.parentViewGroup

/**
 * Created by Allan Wang on 2017-07-14.
 *
 * Handles relative sizes for any view
 * You may delegate all methods to [MeasureSpecDelegate]
 * and call the two methods: [initAttrs] and [onMeasure]
 */
interface MeasureSpecContract {

    /**
     * Width will be calculated as a percentage of the parent
     * This takes precedence over relativeWidth
     */
    var relativeWidthToParent: Float
    /**
     * Height will be calculated as a percentage of the parent
     * This takes precedence over relativeHeight
     */
    var relativeHeightToParent: Float
    /**
     * Width will be calculated based on the measured height
     */
    var relativeWidth: Float
    /**
     * Height will be calculated based on the measure width
     */
    var relativeHeight: Float
    /**
     * Width will be once again calculated from the current measured height
     * This is the last step
     */
    var postRelativeWidth: Float
    /**
     * Height will be once again calculated from the current measured width
     * This is the last step
     */
    var postRelativeHeight: Float

    /**
     * Retrieves relative values from the [AttributeSet]
     * Call this on init
     */
    fun initAttrs(context: Context, attrs: AttributeSet?)

    /**
     * Calculates the final measure specs
     * Call this from [View.onMeasure] and send the Pair result as the specs
     * The pair is of the format (width, height)
     *
     * Example:
     * <pre>
     * {@code
     * override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
     *     val result = onMeasure(this, widthMeasureSpec, heightMeasureSpec)
     *     super.onMeasure(result.first, result.second)
     * }
     * }
     * </pre>
     */
    fun onMeasure(view: View, widthMeasureSpec: Int, heightMeasureSpec: Int): Pair<Int, Int>
}

class MeasureSpecDelegate : MeasureSpecContract {

    override var relativeWidth = -1f
    override var relativeHeight = -1f
    override var relativeWidthToParent = -1f
    override var relativeHeightToParent = -1f
    override var postRelativeWidth: Float = -1f
    override var postRelativeHeight: Float = -1f
    private val parentFrame = Rect()

    override fun initAttrs(context: Context, attrs: AttributeSet?) {
        if (attrs == null) return
        val styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.MeasureSpecDelegate)
        relativeWidth = styledAttrs.getFloat(R.styleable.MeasureSpecDelegate_relativeWidth, relativeWidth)
        relativeHeight = styledAttrs.getFloat(R.styleable.MeasureSpecDelegate_relativeHeight, relativeHeight)
        relativeWidthToParent =
            styledAttrs.getFloat(R.styleable.MeasureSpecDelegate_relativeWidthToParent, relativeWidthToParent)
        relativeHeightToParent =
            styledAttrs.getFloat(R.styleable.MeasureSpecDelegate_relativeHeightToParent, relativeHeightToParent)
        postRelativeWidth = styledAttrs.getFloat(R.styleable.MeasureSpecDelegate_postRelativeWidth, postRelativeWidth)
        postRelativeHeight =
            styledAttrs.getFloat(R.styleable.MeasureSpecDelegate_postRelativeHeight, postRelativeHeight)
        styledAttrs.recycle()
    }

    override fun onMeasure(view: View, widthMeasureSpec: Int, heightMeasureSpec: Int): Pair<Int, Int> {
        view.parentViewGroup.getWindowVisibleDisplayFrame(parentFrame)
        var width = View.MeasureSpec.getSize(widthMeasureSpec).toFloat()
        var height = View.MeasureSpec.getSize(heightMeasureSpec).toFloat()
        //first cycle - relative to parent
        if (relativeHeightToParent > 0)
            height = relativeHeightToParent * parentFrame.height()
        if (relativeWidthToParent > 0)
            width = relativeWidthToParent * parentFrame.width()
        //second cycle - relative to each other
        if (relativeHeight > 0)
            height = relativeHeight * width
        else if (relativeWidth > 0)
            width = relativeWidth * height
        //third cycle - relative to each other
        if (postRelativeHeight > 0)
            height = postRelativeHeight * width
        else if (postRelativeWidth > 0)
            width = postRelativeWidth * height
        return Pair(width.measureSpec, height.measureSpec)
    }

    private val Float.measureSpec: Int
        get() = View.MeasureSpec.makeMeasureSpec(this.toInt(), View.MeasureSpec.EXACTLY)
}
