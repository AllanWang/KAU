package ca.allanwang.kau.swipe

import ca.allanwang.kau.kotlin.nonReadable

/**
 * Created by Mr.Jude on 2015/8/26.
 */
class RelativeSlider(var curPage: SwipeBackPage) : SwipeListener {

    var offset = 0f

    var enabled: Boolean
        @Deprecated(level = DeprecationLevel.ERROR, message = "Cannot use enabled as getter")
        get() = nonReadable()
        set(value) {
            if (value) curPage.addListener(this)
            else curPage.removeListener(this)
        }

    override fun onScroll(percent: Float, px: Int) {
        if (offset == 0f) return //relative slider is not enabled
        val page = SwipeBackHelper.getPrePage(curPage) ?: return
        page.swipeBackLayout.x = Math.min(-offset * Math.max(1 - percent, 0f) + DEFAULT_OFFSET, 0f)
        if (percent == 0f) page.swipeBackLayout.x = 0f
    }

    override fun onEdgeTouch() {}

    override fun onScrollToClose() {
       SwipeBackHelper.getPrePage(curPage)?.swipeBackLayout?.x = 0f
    }

    companion object {
        private const val DEFAULT_OFFSET = 40
    }
}
