package ca.allanwang.kau.swipe

import android.app.Activity
import ca.allanwang.kau.R
import ca.allanwang.kau.kotlin.kauRemoveIf
import ca.allanwang.kau.logging.KL
import ca.allanwang.kau.swipe.SwipeBackHelper.onDestroy
import java.util.*

class SwipeBackException(message: String = "You Should call kauSwipeOnCreate() first") : RuntimeException(message)

/**
 * Singleton to hold our swipe stack
 * All activity pages held with strong references, so it is crucial to call
 * [onDestroy] whenever an activity should be disposed
 */
object SwipeBackHelper {

    private val pageStack = Stack<SwipeBackPage>()

    private operator fun get(activity: Activity): SwipeBackPage?
            = pageStack.firstOrNull { it.activityRef.get() === activity }

    fun onCreate(activity: Activity, builder: SwipeBackContract.() -> Unit = {}) {
        val page = this[activity] ?: pageStack.push(SwipeBackPage(activity).apply { builder() })
        val startAnimation: Int = when (page.edgeFlag) {
            SWIPE_EDGE_LEFT -> R.anim.kau_slide_in_right
            SWIPE_EDGE_RIGHT -> R.anim.kau_slide_in_left
            SWIPE_EDGE_TOP -> R.anim.kau_slide_in_bottom
            else -> R.anim.kau_slide_in_top
        }
        activity.overridePendingTransition(startAnimation, 0)
        KL.v("KauSwipe onCreate ${activity.localClassName}")
    }

    fun onPostCreate(activity: Activity) {
        KL.v("KauSwipe onPostCreate ${activity.localClassName}")
        this[activity]?.onPostCreate() ?: throw SwipeBackException()
    }

    fun onDestroy(activity: Activity) {
        val page: SwipeBackPage? = this[activity]
        pageStack.kauRemoveIf { it.activityRef.get() == null || it === page }
        page?.activityRef?.clear()
        KL.v("KauSwipe onDestroy ${activity.localClassName}")
    }

    fun finish(activity: Activity) = this[activity]?.scrollToFinishActivity()

    internal fun getPrePage(page: SwipeBackPage): SwipeBackPage? {
        //clean invalid pages
        pageStack.kauRemoveIf { it.activityRef.get() == null }
        return pageStack.getOrNull(pageStack.indexOf(page) - 1)
    }
}

/**
 * The following are the activity bindings to add an activity to the stack
 * onCreate, onPostCreate, and onDestroy are mandatory
 * finish is there as a helper method to animate the transaction
 */
fun Activity.kauSwipeOnCreate(builder: SwipeBackContract.() -> Unit = {}) = SwipeBackHelper.onCreate(this, builder)

fun Activity.kauSwipeOnPostCreate() = SwipeBackHelper.onPostCreate(this)
fun Activity.kauSwipeOnDestroy() = SwipeBackHelper.onDestroy(this)
fun Activity.kauSwipeFinish() = SwipeBackHelper.finish(this)

/**
 * Constants used for the swipe edge flags
 */
const val SWIPE_EDGE_LEFT = ViewDragHelper.EDGE_LEFT

const val SWIPE_EDGE_RIGHT = ViewDragHelper.EDGE_RIGHT

const val SWIPE_EDGE_TOP = ViewDragHelper.EDGE_TOP

const val SWIPE_EDGE_BOTTOM = ViewDragHelper.EDGE_BOTTOM