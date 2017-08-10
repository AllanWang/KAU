package ca.allanwang.kau.sample

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.ViewGroup
import android.widget.Button
import ca.allanwang.kau.internal.KauBaseActivity
import ca.allanwang.kau.swipe.*
import ca.allanwang.kau.utils.*

/**
 * Created by Allan Wang on 2017-08-05.
 */
private const val SWIPE_EDGE = "swipe_edge"

fun Activity.startActivityWithEdge(flag: Int) {
    startActivity(SwipeActivity::class.java) {
        putExtra(SWIPE_EDGE, flag)
    }
}

class SwipeActivity : KauBaseActivity() {

    val toolbar: Toolbar by bindView(R.id.swipe_toolbar)
    val container: ViewGroup by bindView(R.id.swipe_container)
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
            it.setOnClickListener { startActivityWithEdge(swipeEdge) }
        }
        val flag = intent.getIntExtra(SWIPE_EDGE, -1)
        toolbar.title = when (flag) {
            SWIPE_EDGE_LEFT -> "Left Edge Swipe"
            SWIPE_EDGE_RIGHT -> "Right Edge Swipe"
            SWIPE_EDGE_TOP -> "Top Edge Swipe"
            SWIPE_EDGE_BOTTOM -> "Bottom Edge Swipe"
            else -> "Invalid Edge Swipe"
        }
        setSupportActionBar(toolbar)
        val headerColor = rndColor.darken(0.6f)
        toolbar.setBackgroundColor(headerColor)
        statusBarColor = headerColor
        val bg = headerColor.darken(0.2f)
        container.setBackgroundColor(bg)
        navigationBarColor = bg
        kauSwipeOnCreate {
            edgeFlag = flag
        }
    }

    override fun onDestroy() {
        kauSwipeOnDestroy()
        super.onDestroy()
    }

    override fun onBackPressed() {
        kauSwipeFinish()
    }
}