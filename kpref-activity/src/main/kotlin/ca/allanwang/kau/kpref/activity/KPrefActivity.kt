package ca.allanwang.kau.kpref.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import ca.allanwang.kau.animators.KauAnimator
import ca.allanwang.kau.animators.SlideAnimatorAdd
import ca.allanwang.kau.animators.SlideAnimatorRemove
import ca.allanwang.kau.internal.KauBaseActivity
import ca.allanwang.kau.kpref.activity.items.KPrefItemCore
import ca.allanwang.kau.ui.views.RippleCanvas
import ca.allanwang.kau.utils.*
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*

abstract class KPrefActivity : KauBaseActivity(), KPrefActivityContract {

    private val adapter: FastItemAdapter<KPrefItemCore> = FastItemAdapter()
    private val recycler: RecyclerView by bindView(R.id.kau_recycler)
    val bgCanvas: RippleCanvas by bindView(R.id.kau_ripple)
    val toolbarCanvas: RippleCanvas by bindView(R.id.kau_toolbar_ripple)
    val toolbar: Toolbar by bindView(R.id.kau_toolbar)
    private lateinit var globalOptions: GlobalOptions
    private val kprefStack = Stack<Pair<Int, List<KPrefItemCore>>>()
    /**
     * Toggle sliding animations for the kpref items
     */
    var animate: Boolean = true

    private val recyclerAnimatorNext: KauAnimator by lazy {
        KauAnimator(SlideAnimatorAdd(KAU_RIGHT, itemDelayFactor = 0f),
                SlideAnimatorRemove(KAU_LEFT, itemDelayFactor = 0f))
    }
    private val recyclerAnimatorPrev: KauAnimator by lazy {
        KauAnimator(SlideAnimatorAdd(KAU_LEFT, itemDelayFactor = 0f),
                SlideAnimatorRemove(KAU_RIGHT, itemDelayFactor = 0f))
    }

    /**
     * Core attribute builder that is consistent throughout all items
     * Leave blank to use defaults
     */
    abstract fun kPrefCoreAttributes(): CoreAttributeContract.() -> Unit

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setup layout
        setContentView(R.layout.kau_pref_activity)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            toolbar.setNavigationOnClickListener { onBackPressed() }
            setDisplayShowTitleEnabled(false)
        }
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        statusBarColor = 0x30000000
        toolbarCanvas.set(resolveColor(R.attr.colorPrimary))
        bgCanvas.set(resolveColor(android.R.attr.colorBackground))
        //setup prefs
        val core = CoreAttributeBuilder()
        val builder = kPrefCoreAttributes()
        core.builder()
        globalOptions = GlobalOptions(core, this)
        recycler.withLinearAdapter(adapter)
        adapter.withSelectable(false)
                .withOnClickListener { v, _, item, _ -> item.onClick(v, v.findViewById(R.id.kau_pref_inner_content)) }
        showNextPrefs(R.string.kau_settings, onCreateKPrefs(savedInstanceState), true)
    }

    override fun showNextPrefs(@StringRes toolbarTitleRes: Int, builder: KPrefAdapterBuilder.() -> Unit)
            = showNextPrefs(toolbarTitleRes, builder, false)

    private fun showNextPrefs(@StringRes toolbarTitleRes: Int, builder: KPrefAdapterBuilder.() -> Unit, first: Boolean) {
        doAsync {
            val items = KPrefAdapterBuilder(globalOptions)
            builder(items)
            kprefStack.push(toolbarTitleRes to items.list)
            recycler.itemAnimator = if (animate && !first) recyclerAnimatorNext else null
            uiThread {
                adapter.clear()
                adapter.add(items.list)
                toolbar.setTitle(toolbarTitleRes)
            }
        }
    }

    override fun showPrevPrefs() {
        kprefStack.pop()
        val (title, list) = kprefStack.peek()
        recycler.itemAnimator = if (animate) recyclerAnimatorPrev else null
        adapter.clear()
        adapter.add(list)
        toolbar.setTitle(title)
    }

    fun reload(vararg index: Int) {
        if (index.isEmpty()) adapter.notifyAdapterDataSetChanged()
        else index.forEach { adapter.notifyItemChanged(it) }
    }

    override fun reloadByTitle(@StringRes vararg title: Int) {
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
        if (kprefStack.size > 1) {
            showPrevPrefs()
            return true
        }
        return false
    }
}

