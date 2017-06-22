package ca.allanwang.kau.kpref

import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ViewAnimator
import ca.allanwang.kau.R
import ca.allanwang.kau.kpref.items.KPrefItemCore
import ca.allanwang.kau.utils.bindView
import ca.allanwang.kau.utils.resolveColor
import ca.allanwang.kau.utils.statusBarColor
import ca.allanwang.kau.views.RippleCanvas
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter

abstract class KPrefActivity : AppCompatActivity(), KPrefActivityContract {

    val adapter: FastItemAdapter<KPrefItemCore>
        get() = recycler.adapter as FastItemAdapter<KPrefItemCore>
    val recycler: RecyclerView
        get() = prefHolder.currentView as RecyclerView
    lateinit var baseRecycler: RecyclerView
    val bgCanvas: RippleCanvas by bindView(R.id.kau_ripple)
    val toolbarCanvas: RippleCanvas by bindView(R.id.kau_toolbar_ripple)
    val toolbar: Toolbar by bindView(R.id.kau_toolbar)
    val prefHolder: ViewAnimator by bindView(R.id.kau_holder)
    private lateinit var globalOptions: GlobalOptions

    /**
     * Core attribute builder that is consistent throughout all items
     * Leave blank to use defaults
     */
    abstract fun kPrefCoreAttributes(): CoreAttributeContract.() -> Unit

    init {
        setup()
    }

    private fun setup() {
        val core = CoreAttributeBuilder()
        val builder = kPrefCoreAttributes()
        core.builder()
        globalOptions = GlobalOptions(core, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.kau_activity_kpref)
        setSupportActionBar(toolbar)
        if (supportActionBar != null)
            with(supportActionBar!!) {
                setDisplayHomeAsUpEnabled(true)
                setDisplayShowHomeEnabled(true)
                toolbar.setNavigationOnClickListener { onBackPressed() }
            }
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        statusBarColor = 0x30000000
        toolbarCanvas.set(resolveColor(R.attr.colorPrimary))
        bgCanvas.set(resolveColor(android.R.attr.colorBackground))
        with(prefHolder) {
            animateFirstView = false
            inAnimation = AnimationUtils.loadAnimation(context, R.anim.kau_slide_in_right)
            outAnimation = AnimationUtils.loadAnimation(context, R.anim.kau_slide_out_left)
        }
        showNextPrefs(onCreateKPrefs(savedInstanceState))
        baseRecycler = recycler
    }

    override fun showNextPrefs(builder: KPrefAdapterBuilder.() -> Unit) {
        val rv = RecyclerView(this).apply {
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            setKPrefAdapter(globalOptions, builder)
        }
        with(prefHolder) {
            inAnimation = AnimationUtils.loadAnimation(context, R.anim.kau_slide_in_right)
            outAnimation = AnimationUtils.loadAnimation(context, R.anim.kau_slide_out_left)
            addView(rv)
            showNext()
        }
    }

    override fun showPrevPrefs() {
        val current = prefHolder.currentView
        with(prefHolder) {
            inAnimation = AnimationUtils.loadAnimation(context, R.anim.kau_slide_in_left)
            outAnimation = AnimationUtils.loadAnimation(context, R.anim.kau_slide_out_right)
            showPrevious()
            removeView(current)
        }
    }

    fun reload(vararg index: Int) {
        if (index.isEmpty()) adapter.notifyAdapterDataSetChanged()
        else index.forEach { adapter.notifyItemChanged(it) }
    }

    fun reloadByTitle(@StringRes vararg title: Int) {
        if (title.isEmpty()) return
        adapter.adapterItems.forEachIndexed { index, item ->
            if (title.any { item.core.titleRes == it })
                adapter.notifyItemChanged(index)
        }
    }

    abstract fun onCreateKPrefs(savedInstanceState: Bundle?): KPrefAdapterBuilder.() -> Unit

    override fun onBackPressed() {
        if (!backPress()) super.onBackPressed()
    }

    fun backPress(): Boolean {
        if (baseRecycler != recycler) {
            showPrevPrefs()
            return true
        }
        return false
    }
}

