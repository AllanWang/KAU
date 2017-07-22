package ca.allanwang.kau.ui.activities

import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.transition.TransitionInflater
import ca.allanwang.kau.ui.R
import ca.allanwang.kau.ui.widgets.ElasticDragDismissFrameLayout
import ca.allanwang.kau.utils.bindView

/**
 * Created by Allan Wang on 2017-07-17.
 *
 * A generic activity comprised of an ElasticDragDismissFrameLayout, CoordinatorLayout, Toolbar, RecyclerView, and Fab
 * [ca.allanwang.kau.ui.widgets.ElasticDragDismissFrameLayout]
 * [android.support.v7.widget.RecyclerView]
 *
 * The recyclerview defaults to a linearlayoutmanager, and the adapter is automatically bounded
 *
 * The exit animation is set to slide out, but the entrance must be defined yourself
 */
abstract class ElasticRecyclerActivity() : AppCompatActivity() {

    val appBar: AppBarLayout by bindView(R.id.kau_appbar)
    val toolbar: Toolbar by bindView(R.id.kau_toolbar)
    val coordinator: CoordinatorLayout by bindView(R.id.kau_coordinator)
    val draggableFrame: ElasticDragDismissFrameLayout by bindView(R.id.kau_draggable)
    val recycler: RecyclerView by bindView(R.id.kau_recycler)
    val fab: FloatingActionButton by bindView(R.id.kau_fab)
    val configs = Configs()

    class Configs {
        var exitTransitionBottom = R.transition.kau_exit_slide_bottom
        var exitTransitionTop = R.transition.kau_exit_slide_top
    }

    override final fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.kau_elastic_recycler_activity)
        setSupportActionBar(toolbar)
        if (!onCreate(savedInstanceState, configs)) return
        draggableFrame.addListener(object : ElasticDragDismissFrameLayout.SystemChromeFader(this) {
            override fun onDragDismissed() {
                window.returnTransition = TransitionInflater.from(this@ElasticRecyclerActivity)
                        .inflateTransition(if (draggableFrame.translationY > 0) configs.exitTransitionBottom else configs.exitTransitionTop)
                recycler.stopScroll()
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
        draggableFrame.setOnClickListener { listener() }
    }

    fun hideFabOnUpwardsScroll() {
        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE && !fab.isShown()) fab.show();
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0 && fab.isShown) fab.hide()
                else if (dy < 0 && !fab.isShown) fab.show()
            }
        })
    }

}

