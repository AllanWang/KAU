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
package ca.allanwang.kau.ui.activities

import android.os.Build
import android.os.Bundle
import android.transition.TransitionInflater
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import ca.allanwang.kau.internal.KauBaseActivity
import ca.allanwang.kau.ui.R
import ca.allanwang.kau.ui.databinding.KauElasticRecyclerActivityBinding
import ca.allanwang.kau.ui.widgets.ElasticDragDismissFrameLayout

/**
 * Created by Allan Wang on 2017-07-17.
 *
 * A generic activity comprised of an ElasticDragDismissFrameLayout, CoordinatorLayout, Toolbar, RecyclerView, and Fab
 * [ca.allanwang.kau.ui.widgets.ElasticDragDismissFrameLayout]
 * [androidx.recyclerview.widget.RecyclerView]
 *
 * The recyclerview defaults to a linearlayoutmanager, and the adapter is automatically bounded
 *
 * The exit animation is set to slide out, but the entrance must be defined yourself
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
abstract class ElasticRecyclerActivity : KauBaseActivity() {

    private val configs = Configs()

    private lateinit var binding: KauElasticRecyclerActivityBinding

    protected val toolbar: Toolbar get() = binding.kauToolbar
    protected val recycler: RecyclerView get() = binding.kauRecycler

    class Configs {
        var exitTransitionBottom = R.transition.kau_exit_slide_bottom
        var exitTransitionTop = R.transition.kau_exit_slide_top
    }

    final override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = KauElasticRecyclerActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.kauToolbar)
        if (!onCreate(savedInstanceState, configs)) {
            return
        }

        binding.init()
    }

    private fun KauElasticRecyclerActivityBinding.init() {
        kauDraggable.addListener(object : ElasticDragDismissFrameLayout.SystemChromeFader(this@ElasticRecyclerActivity) {
            override fun onDragDismissed() {
                window.returnTransition = TransitionInflater.from(this@ElasticRecyclerActivity)
                        .inflateTransition(if (kauDraggable.translationY > 0) configs.exitTransitionBottom else configs.exitTransitionTop)
                kauRecycler.stopScroll()
                finishAfterTransition()
            }
        })
    }

    /**
     * The replacement method for the original [onCreate]
     * The configurations are passed and can be customized here
     * Returns true (default) if we wish to continue with the remaining optional setup
     * Return false if we wish to skip this (usually if we have more complez requirements)
     */
    abstract fun onCreate(savedInstanceState: Bundle?, configs: Configs): Boolean

    /**
     * Receive actions when the a click event is received outside of the coordinator
     */
    fun setOutsideTapListener(listener: () -> Unit) {
        binding.kauDraggable.setOnClickListener { listener() }
    }
}
