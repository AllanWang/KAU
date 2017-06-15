package ca.allanwang.kau.kpref

import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import ca.allanwang.kau.R
import ca.allanwang.kau.kpref.items.KPrefItemCore
import ca.allanwang.kau.utils.bindView
import ca.allanwang.kau.utils.resolveColor
import ca.allanwang.kau.utils.statusBarColor
import ca.allanwang.kau.views.RippleCanvas
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter

abstract class KPrefActivity : AppCompatActivity() {

    lateinit var adapter: FastItemAdapter<KPrefItemCore>
    val recycler: RecyclerView by bindView(R.id.kau_recycler)
    val bgCanvas: RippleCanvas by bindView(R.id.kau_ripple)
    val toolbarCanvas: RippleCanvas by bindView(R.id.kau_toolbar_ripple)
    val toolbar: Toolbar by bindView(R.id.kau_toolbar)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.kau_activity_kpref)
        setSupportActionBar(toolbar)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        statusBarColor = 0x30000000
        toolbarCanvas.set(resolveColor(R.attr.colorPrimary))
        bgCanvas.set(resolveColor(android.R.attr.colorBackground))
        adapter = recycler.setKPrefAdapter(onCreateKPrefs(savedInstanceState))
    }

    fun reload(vararg index: Int) {
        if (index.isEmpty()) adapter.notifyAdapterDataSetChanged()
        else index.forEach { adapter.notifyItemChanged(it) }
    }

    fun reloadByTitle(@StringRes vararg title: Int) {
        if (title.isEmpty()) return
        adapter.adapterItems.forEachIndexed { index, item ->
            if (title.any { item.title == it })
                adapter.notifyItemChanged(index)
        }
    }

    abstract fun onCreateKPrefs(savedInstanceState: Bundle?): KPrefAdapterBuilder.() -> Unit

}

