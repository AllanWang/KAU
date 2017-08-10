package ca.allanwang.kau.swipe

import android.app.Activity
import ca.allanwang.kau.R
import ca.allanwang.kau.kotlin.kauRemoveIf
import ca.allanwang.kau.logging.KL
import ca.allanwang.kau.swipe.SwipeBackHelper.onDestroy
import java.util.*

/**
 * Singleton to hold our swipe stack
 * All activity pages held with strong references, so it is crucial to call
 * [onDestroy] whenever an activity should be disposed
 */
internal object SwipeBackHelper {

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
        page.onPostCreate()
        KL.v("KauSwipe onCreate ${activity.localClassName}")
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
 * The creation binder, which adds the swipe functionality to an activity.
 * Call this during [Activity.onCreate] after all views are added.
 *
 * Preferably, this should be the last line in the onCreate method.
 * Note that this will also capture your statusbar color and nav bar color,
 * so be sure to assign those beforehand if at all.
 *
 * Lastly, don't forget to call [kauSwipeOnDestroy] as well when the activity is destroyed.
 */
fun Activity.kauSwipeOnCreate(builder: SwipeBackContract.() -> Unit = {}) = SwipeBackHelper.onCreate(this, builder)

/**
 * Deprecated as onPostCreate seems unreliable.
 * We will instead initialize everything during [kauSwipeOnCreate]
 */
@Deprecated(level = DeprecationLevel.WARNING, message = "All functionality has been moved to kauSwipeOnCreate")
fun Activity.kauSwipeOnPostCreate() {
}

/**
 * The unbinder, which removes our layouts, releases our weak references, and cleans our page stack
 * Call this during [Activity.onDestroy]
 *
 * Given that our references are held weakly, we allow failures and missing pages to pass silently
 * as they should be destroyed anyways.
 *
 * Don't forget to call [kauSwipeOnCreate] when the activity is created to enable swipe functionality.
 */
fun Activity.kauSwipeOnDestroy() = SwipeBackHelper.onDestroy(this)

/**
 * Helper function for activities to animate the finish transaction with a pseudo swipe
 * The activity will automatically be finished afterwards
 */
fun Activity.kauSwipeFinish() = SwipeBackHelper.finish(this)

/*
 * Constants used for the swipe edge flags
 */
const val SWIPE_EDGE_LEFT = ViewDragHelper.EDGE_LEFT

const val SWIPE_EDGE_RIGHT = ViewDragHelper.EDGE_RIGHT

const val SWIPE_EDGE_TOP = ViewDragHelper.EDGE_TOP

const val SWIPE_EDGE_BOTTOM = ViewDragHelper.EDGE_BOTTOM