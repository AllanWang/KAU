package ca.allanwang.kau.sample

import android.app.Activity
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import ca.allanwang.kau.internal.KauBaseActivity
import ca.allanwang.kau.swipe.*
import ca.allanwang.kau.utils.bindViews
import ca.allanwang.kau.utils.startActivity

/**
 * Created by Allan Wang on 2017-08-05.
 */
class SwipeActivity : KauBaseActivity() {

    companion object {
        private const val SWIPE_EDGE = "swipe_edge"
        fun Activity.startActivityFromEdge(flag: Int) {
            startActivity(SwipeActivity::class.java) {
                putExtra(SWIPE_EDGE, flag)
            }
        }
    }

    val directions: List<Button> by bindViews(R.id.swipe_from_left, R.id.swipe_from_right, R.id.swipe_from_top, R.id.swipe_from_bottom)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_swipe)
        directions.forEach {
            val swipeEdge = when (it.id) {
                R.id.swipe_from_left -> SWIPE_EDGE_LEFT
                R.id.swipe_from_right -> SWIPE_EDGE_RIGHT
                R.id.swipe_from_top -> SWIPE_EDGE_TOP
                R.id.swipe_from_bottom -> SWIPE_EDGE_BOTTOM
                else -> -1
            }
            it.setOnClickListener { startActivityFromEdge(swipeEdge) }
        }
        kauSwipeOnCreate {
            edgeFlag = intent.getIntExtra(SWIPE_EDGE, -1)
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onPostCreate(savedInstanceState, persistentState)
        kauSwipeOnPostCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
        kauSwipeOnDestroy()
    }
}