package ca.allanwang.kau.swipe

import android.app.Activity
import ca.allanwang.kau.R
import ca.allanwang.kau.logging.KL
import java.util.*

class SwipeBackException(message: String = "You Should call SwipeBackHelper.onCreate(activity) first") : RuntimeException(message)

object SwipeBackHelper {

    private val pageStack = Stack<SwipeBackPage>()

    private operator fun get(activity: Activity): SwipeBackPage
            = pageStack.firstOrNull { it.activity === activity } ?: throw SwipeBackException()

    fun getCurrentPage(activity: Activity): SwipeBackPage = this[activity]

    fun onCreate(activity: Activity, builder: SwipeBackPage.() -> Unit = {}) {
        val page = pageStack.firstOrNull { it.activity === activity } ?: pageStack.push(SwipeBackPage(activity).apply { builder() })
        val startAnimation: Int = with(page.edgeFlag) {
            if (this and SWIPE_EDGE_LEFT > 0) R.anim.kau_slide_in_right
            else if (this and SWIPE_EDGE_RIGHT > 0) R.anim.kau_slide_in_left
            else if (this and SWIPE_EDGE_TOP > 0) R.anim.kau_slide_in_bottom
            else R.anim.kau_slide_in_top
        }
        activity.overridePendingTransition(startAnimation, 0)
    }

    fun onPostCreate(activity: Activity) = this[activity].onPostCreate()

    fun onDestroy(activity: Activity) {
        KL.d("Swipe destroy")
        val page: SwipeBackPage = this[activity]
        pageStack.remove(page)
        page.activity = null
    }

    fun finish(activity: Activity) = this[activity].scrollToFinishActivity()

    internal fun getPrePage(activity: SwipeBackPage): SwipeBackPage? {
        val index = pageStack.indexOf(activity)
        return if (index > 0) pageStack[index - 1] else null
    }

}

fun Activity.kauSwipeOnCreate(builder: SwipeBackPage.() -> Unit = {}) = SwipeBackHelper.onCreate(this, builder)
fun Activity.kauSwipeOnPostCreate() = SwipeBackHelper.onPostCreate(this)
fun Activity.kauSwipeOnDestroy() = SwipeBackHelper.onDestroy(this)
fun Activity.kauSwipeFinish() = SwipeBackHelper.finish(this)

const val SWIPE_EDGE_LEFT = ViewDragHelper.EDGE_LEFT

const val SWIPE_EDGE_RIGHT = ViewDragHelper.EDGE_RIGHT

const val SWIPE_EDGE_TOP = ViewDragHelper.EDGE_TOP

const val SWIPE_EDGE_BOTTOM = ViewDragHelper.EDGE_BOTTOM

const val SWIPE_EDGE_HORIZONTAL = SWIPE_EDGE_LEFT or SWIPE_EDGE_RIGHT

const val SWIPE_EDGE_VERTICAL = SWIPE_EDGE_TOP or SWIPE_EDGE_BOTTOM

const val SWIPE_EDGE_ALL = SWIPE_EDGE_HORIZONTAL or SWIPE_EDGE_VERTICAL