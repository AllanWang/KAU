/*
 * Copyright 2018 Allan Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ca.allanwang.kau.sample

import android.app.Activity
import android.os.Bundle
import ca.allanwang.kau.internal.KauBaseActivity
import ca.allanwang.kau.sample.databinding.ActivitySwipeBinding
import ca.allanwang.kau.swipe.SWIPE_EDGE_BOTTOM
import ca.allanwang.kau.swipe.SWIPE_EDGE_LEFT
import ca.allanwang.kau.swipe.SWIPE_EDGE_RIGHT
import ca.allanwang.kau.swipe.SWIPE_EDGE_TOP
import ca.allanwang.kau.swipe.kauSwipeFinish
import ca.allanwang.kau.swipe.kauSwipeOnCreate
import ca.allanwang.kau.swipe.kauSwipeOnDestroy
import ca.allanwang.kau.utils.darken
import ca.allanwang.kau.utils.navigationBarColor
import ca.allanwang.kau.utils.rndColor
import ca.allanwang.kau.utils.startActivity
import ca.allanwang.kau.utils.statusBarColor

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

    private lateinit var binding: ActivitySwipeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySwipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            mapOf(
                swipeFromLeft to SWIPE_EDGE_LEFT,
                swipeFromRight to SWIPE_EDGE_RIGHT,
                swipeFromTop to SWIPE_EDGE_TOP,
                swipeFromBottom to SWIPE_EDGE_BOTTOM
            ).forEach { (button, edge) ->
                button.setOnClickListener { startActivityWithEdge(edge) }
            }
            val flag = intent.getIntExtra(SWIPE_EDGE, -1)
            swipeToolbar.title = when (flag) {
                SWIPE_EDGE_LEFT -> "Left Edge Swipe"
                SWIPE_EDGE_RIGHT -> "Right Edge Swipe"
                SWIPE_EDGE_TOP -> "Top Edge Swipe"
                SWIPE_EDGE_BOTTOM -> "Bottom Edge Swipe"
                else -> "Invalid Edge Swipe"
            }
            setSupportActionBar(swipeToolbar)
            val headerColor = rndColor.darken(0.6f)
            swipeToolbar.setBackgroundColor(headerColor)
            statusBarColor = headerColor
            val bg = headerColor.darken(0.2f)
            swipeContainer.setBackgroundColor(bg)
            navigationBarColor = bg
            kauSwipeOnCreate {
                edgeFlag = flag
            }
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
