package ca.allanwang.kau.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import ca.allanwang.kau.kpref.KPrefAdapterBuilder
import ca.allanwang.kau.kpref.items.KPrefItemCore
import ca.allanwang.kau.kpref.setKPrefAdapter
import ca.allanwang.kau.utils.showChangelog
import ca.allanwang.kau.views.RippleCanvas
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter

class MainActivity : AppCompatActivity() {

    lateinit var adapter: FastItemAdapter<KPrefItemCore>
    lateinit var builder: KPrefAdapterBuilder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val recycler = RecyclerView(this)
        val bgCanvas = RippleCanvas(this)
        setContentView(bgCanvas)
        addContentView(recycler, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        bgCanvas.set(KPrefSample.bgColor)
        adapter = recycler.setKPrefAdapter {
            builder = this
            textColor = KPrefSample.textColor
            header(R.string.header)
            checkbox(title = R.string.checkbox_1, description = R.string.desc,
                    getter = { KPrefSample.check1 }, setter = { KPrefSample.check1 = it })
            checkbox(title = R.string.checkbox_2,
                    getter = { KPrefSample.check2 }, setter = { KPrefSample.check2 = it })
            checkbox(title = R.string.checkbox_3, description = R.string.desc_disabled, enabled = false,
                    getter = { KPrefSample.check3 }, setter = { KPrefSample.check3 = it })
            colorPicker(title = R.string.text_color,
                    getter = { KPrefSample.textColor }, setter = { KPrefSample.textColor = it; builder.textColor = it; refresh() },
                    configs = {
                        allowCustom = false
                    })
            colorPicker(title = R.string.background_color,
                    getter = { KPrefSample.bgColor }, setter = { KPrefSample.bgColor = it; bgCanvas.ripple(it, duration = 500) },
                    configs = {
                        allowCustomAlpha = true
                        allowCustom = true
                    })
        }
    }

    fun refresh() {
        adapter.notifyAdapterDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {

            }
            R.id.action_changelog -> showChangelog(R.xml.kau_changelog)
            R.id.action_call -> {
            }
            R.id.action_db -> {
            }
            R.id.action_restart -> {
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

}
