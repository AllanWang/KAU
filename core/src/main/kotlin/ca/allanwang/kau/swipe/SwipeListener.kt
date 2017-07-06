package ca.allanwang.kau.swipe

interface SwipeListener {
    /**
     * Invoked as the page is scrolled
     * Percent is capped at 1.0, even if there is a slight overscroll for the pages
     */
    fun onScroll(percent: Float, px: Int, edgeFlag: Int)
    /**
     * Invoked when page first consumes the scroll events
     */
    fun onEdgeTouch()
    /**
     * Invoked when scroll percent over the threshold for the first time
     */
    fun onScrollToClose(edgeFlag: Int)
}