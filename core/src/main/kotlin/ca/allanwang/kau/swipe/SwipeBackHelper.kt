package ca.allanwang.kau.swipe

import android.app.Activity
import ca.allanwang.kau.logging.KL
import java.util.*

class SwipeBackException(message: String = "You Should call SwipeBackHelper.onCreate(activity) first") : RuntimeException(message)

object SwipeBackHelper {

    private val pageStack = Stack<SwipeBackPage>()

    private operator fun get(activity: Activity): SwipeBackPage
            = pageStack.firstOrNull { it.activity === activity } ?: throw SwipeBackException()

    fun getCurrentPage(activity: Activity): SwipeBackPage = this[activity]

    fun onCreate(activity: Activity) {
        activity.overridePendingTransition(0, 0)
        pageStack.firstOrNull { it.activity === activity } ?: pageStack.push(SwipeBackPage(activity))
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

fun Activity.kauSwipeOnCreate() = SwipeBackHelper.onCreate(this)
fun Activity.kauSwipeOnPostCreate() = SwipeBackHelper.onPostCreate(this)
fun Activity.kauSwipeOnDestroy() = SwipeBackHelper.onDestroy(this)
fun Activity.kauSwipeFinish() = SwipeBackHelper.finish(this)
