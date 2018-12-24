package ca.allanwang.kau.ui.activities

import android.os.Build
import android.os.Bundle
import android.transition.TransitionInflater
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import ca.allanwang.kau.internal.KauBaseActivity
import ca.allanwang.kau.ui.R
import ca.allanwang.kau.ui.widgets.ElasticDragDismissFrameLayout
import kotlinx.android.synthetic.main.kau_elastic_recycler_activity.*

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
    protected val toolbar: Toolbar get() = kau_toolbar
    protected val recycler: RecyclerView get() = kau_recycler

    class Configs {
        var exitTransitionBottom = R.transition.kau_exit_slide_bottom
        var exitTransitionTop = R.transition.kau_exit_slide_top
    }

    final override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.kau_elastic_recycler_activity)
        setSupportActionBar(kau_toolbar)
        if (!onCreate(savedInstanceState, configs)) return

        kau_draggable.addListener(object : ElasticDragDismissFrameLayout.SystemChromeFader(this) {
            override fun onDragDismissed() {
                window.returnTransition = TransitionInflater.from(this@ElasticRecyclerActivity)
                    .inflateTransition(if (kau_draggable.translationY > 0) configs.exitTransitionBottom else configs.exitTransitionTop)
                kau_recycler.stopScroll()
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
        kau_draggable.setOnClickListener { listener() }
    }
}

