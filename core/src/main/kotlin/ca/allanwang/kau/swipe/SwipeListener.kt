package ca.allanwang.kau.swipe

interface SwipeListener {
    fun onScroll(percent: Float, px: Int, edgeFlag: Int)
    fun onEdgeTouch()
    /**
     * Invoke when scroll percent over the threshold for the first time
     */
    fun onScrollToClose(edgeFlag: Int)
}