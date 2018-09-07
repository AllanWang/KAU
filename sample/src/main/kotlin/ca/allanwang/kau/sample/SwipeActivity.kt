package ca.allanwang.kau.sample

import android.app.Activity
import android.os.Bundle
import ca.allanwang.kau.internal.KauBaseActivity
import ca.allanwang.kau.swipe.*
import ca.allanwang.kau.utils.*
import kotlinx.android.synthetic.main.activity_swipe.*

/**
 * Created by Allan Wang on 2017-08-05.
 */
private const val SWIPE_EDGE = "swipe_edge"

fun Activity.startActivityWithEdge(flag: Int) {
    startActivity<SwipeActivity> {
        putExtra(SWIPE_EDGE, flag)
    }
}

class SwipeActivity : KauBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_swipe)
        listOf(swipe_from_left, swipe_from_right, swipe_from_top, swipe_from_bottom)
                .zip(listOf(SWIPE_EDGE_LEFT, SWIPE_EDGE_RIGHT, SWIPE_EDGE_TOP, SWIPE_EDGE_BOTTOM))
                .forEach { (button, edge) -> button.setOnClickListener { startActivityWithEdge(edge) } }
        val flag = intent.getIntExtra(SWIPE_EDGE, -1)
        swipe_toolbar.title = when (flag) {
            SWIPE_EDGE_LEFT -> "Left Edge Swipe"
            SWIPE_EDGE_RIGHT -> "Right Edge Swipe"
            SWIPE_EDGE_TOP -> "Top Edge Swipe"
            SWIPE_EDGE_BOTTOM -> "Bottom Edge Swipe"
            else -> "Invalid Edge Swipe"
        }
        setSupportActionBar(swipe_toolbar)
        val headerColor = rndColor.darken(0.6f)
        swipe_toolbar.setBackgroundColor(headerColor)
        statusBarColor = headerColor
        val bg = headerColor.darken(0.2f)
        swipe_container.setBackgroundColor(bg)
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