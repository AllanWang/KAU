package ca.allanwang.kau.about

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import ca.allanwang.kau.R
import ca.allanwang.kau.utils.bindView
import ca.allanwang.kau.views.TextSlider

/**
 * Created by Allan Wang on 2017-06-26.
 */
open class AboutActivity : AppCompatActivity() {

    val toolbar: Toolbar by bindView(R.id.kau_toolbar)
    val toolbarText: TextSlider by bindView(R.id.kau_toolbar_text)
    val recycler: RecyclerView by bindView(R.id.kau_recycler)

    protected val SLIDE_IN_UP: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.kau_slide_in_top) }
    protected val SLIDE_IN_DOWN: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.kau_slide_in_bottom) }

    protected val SLIDE_OUT_UP: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.kau_slide_out_top) }
    protected val SLIDE_OUT_DOWN: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.kau_slide_out_bottom) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.kau_activity_about)

    }
}