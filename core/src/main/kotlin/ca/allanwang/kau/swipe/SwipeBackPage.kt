package ca.allanwang.kau.swipe

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import ca.allanwang.kau.logging.KL

/**
 * Created by Mr.Jude on 2015/8/3.
 * 每个滑动页面的管理
 */
class SwipeBackPage(activity: Activity) : SwipeBackPageContract by SwipeBackLayout(activity) {

    var activity: Activity? = activity
    var slider: RelativeSlider

    /**
     * initializing is the equivalent to onCreate
     * since that is when the page is created
     */
    init {
        activity.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        activity.window.decorView.setBackgroundColor(Color.TRANSPARENT)
        swipeBackLayout.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        slider = RelativeSlider(this)
    }

    fun onPostCreate() {
        handleLayout()
    }

    override var swipeEnabled: Boolean
        get() = swipeBackLayout.swipeEnabled
        set(value) {
            swipeBackLayout.swipeEnabled = value
            handleLayout()
        }

    private fun handleLayout() {
        KL.d("Handle layout")
        if (swipeEnabled) swipeBackLayout.attachToActivity(activity!!)
        else swipeBackLayout.removeFromActivity(activity!!)
    }


    //触发关闭Activity百分比
    fun setClosePercent(percent: Float): SwipeBackPage {
        swipeBackLayout.scrollThreshold=percent
        return this
    }

}

interface SwipeBackPageContract {
    var swipeEnabled: Boolean
    var scrimColor:Int
    val swipeBackLayout: SwipeBackLayout
    var scrollThreshold:Float
    var disallowIntercept:Boolean
    fun setEdgeSize(swipeEdge: Int)
    fun setEdgeSizePercent(swipeEdgePercent: Float)
    fun addListener(listener: SwipeListener)
    fun removeListener(listener: SwipeListener)
    fun scrollToFinishActivity()
}
