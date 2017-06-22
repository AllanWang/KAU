package ca.allanwang.kau.kpref

import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.widget.TextViewCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.ViewAnimator
import ca.allanwang.kau.R
import ca.allanwang.kau.kpref.items.KPrefItemCore
import ca.allanwang.kau.logging.KL
import ca.allanwang.kau.utils.bindView
import ca.allanwang.kau.utils.resolveColor
import ca.allanwang.kau.utils.statusBarColor
import ca.allanwang.kau.utils.string
import ca.allanwang.kau.views.RippleCanvas
import ca.allanwang.kau.views.TextSwitcherThemed
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import java.util.*

abstract class KPrefActivity : AppCompatActivity(), KPrefActivityContract {

    val adapter: FastItemAdapter<KPrefItemCore>
        @Suppress("UNCHECKED_CAST")
        get() = recycler.adapter as FastItemAdapter<KPrefItemCore>
    val recycler: RecyclerView
        get() = prefHolder.currentView as RecyclerView
    val bgCanvas: RippleCanvas by bindView(R.id.kau_ripple)
    val toolbarCanvas: RippleCanvas by bindView(R.id.kau_toolbar_ripple)
    val toolbar: Toolbar by bindView(R.id.kau_toolbar)
    val toolbarTitle: TextSwitcherThemed by bindView(R.id.kau_toolbar_text)
    val prefHolder: ViewAnimator by bindView(R.id.kau_holder)
    private lateinit var globalOptions: GlobalOptions
    private val titleStack: Stack<Int> = Stack()
    var animate: Boolean = true
    val isRootPref: Boolean
        get() = titleStack.size == 1

    //we can't use the same animations for both views; otherwise the durations will sync
    private val SLIDE_IN_LEFT_TITLE: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.kau_slide_in_left) }
    private val SLIDE_IN_RIGHT_TITLE: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.kau_slide_in_right) }
    private val SLIDE_OUT_LEFT_TITLE: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.kau_slide_out_left) }
    private val SLIDE_OUT_RIGHT_TITLE: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.kau_slide_out_right) }

    private val SLIDE_IN_LEFT_ITEMS: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.kau_slide_in_left) }
    private val SLIDE_IN_RIGHT_ITEMS: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.kau_slide_in_right) }
    private val SLIDE_OUT_LEFT_ITEMS: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.kau_slide_out_left) }
    private val SLIDE_OUT_RIGHT_ITEMS: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.kau_slide_out_right) }

    /**
     * Core attribute builder that is consistent throughout all items
     * Leave blank to use defaults
     */
    abstract fun kPrefCoreAttributes(): CoreAttributeContract.() -> Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setup layout
        setContentView(R.layout.kau_activity_kpref)
        setSupportActionBar(toolbar)
        if (supportActionBar != null)
            with(supportActionBar!!) {
                setDisplayHomeAsUpEnabled(true)
                setDisplayShowHomeEnabled(true)
                toolbar.setNavigationOnClickListener { onBackPressed() }
                setDisplayShowTitleEnabled(false)
            }
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        statusBarColor = 0x30000000
        toolbarCanvas.set(resolveColor(R.attr.colorPrimary))
        bgCanvas.set(resolveColor(android.R.attr.colorBackground))
        prefHolder.animateFirstView = false
        //setup prefs
        val core = CoreAttributeBuilder()
        val builder = kPrefCoreAttributes()
        core.builder()
        globalOptions = GlobalOptions(core, this)
        with(toolbarTitle) {
            setFactory {
                TextView(this@KPrefActivity).apply {
                    //replica of toolbar title
                    gravity = Gravity.START
                    setSingleLine()
                    ellipsize = TextUtils.TruncateAt.END
                    TextViewCompat.setTextAppearance(this, R.style.TextAppearance_AppCompat_Title)
                }
            }
        }
        showNextPrefs(R.string.kau_settings, onCreateKPrefs(savedInstanceState))
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
    }

    override fun showNextPrefs(@StringRes toolbarTitleRes: Int, builder: KPrefAdapterBuilder.() -> Unit) {
        val rv = RecyclerView(this).apply {
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            titleStack.push(toolbarTitleRes)
            setKPrefAdapter(globalOptions, builder)
        }
        with(prefHolder) {
            inAnimation = if (animate) SLIDE_IN_RIGHT_ITEMS else null
            outAnimation = if (animate) SLIDE_OUT_LEFT_ITEMS else null
            addView(rv)
            showNext()
        }
        with(toolbarTitle) {
            inAnimation = if (animate) SLIDE_IN_RIGHT_TITLE else null
            outAnimation = if (animate) SLIDE_OUT_LEFT_TITLE else null
            setText(string(titleStack.peek()))
        }
    }

    override fun showPrevPrefs() {
        if (titleStack.size <= 1) {
            KL.e("Cannot go back in KPrefActivity; already at index ${titleStack.size - 1}")
            return
        }
        val current = prefHolder.currentView
        with(prefHolder) {
            inAnimation = if (animate) SLIDE_IN_LEFT_ITEMS else null
            outAnimation = if (animate) SLIDE_OUT_RIGHT_ITEMS else null
            showPrevious()
            removeView(current)
            adapter.notifyAdapterDataSetChanged()
        }
        titleStack.pop()
        with(toolbarTitle) {
            inAnimation = if (animate) SLIDE_IN_LEFT_TITLE else null
            outAnimation = if (animate) SLIDE_OUT_RIGHT_TITLE else null
            setText(string(titleStack.peek()))
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
        if (!isRootPref) {
            showPrevPrefs()
            return true
        }
        return false
    }
}

