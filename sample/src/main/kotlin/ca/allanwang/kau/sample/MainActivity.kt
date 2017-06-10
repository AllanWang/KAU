package ca.allanwang.kau.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import ca.allanwang.kau.kpref.setKPrefAdapter
import ca.allanwang.kau.utils.showChangelog

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val recycler = RecyclerView(this)
//        recycler.matchParent()
        setContentView(recycler)
        recycler.setKPrefAdapter {
            header(R.string.header)
            checkbox(title = R.string.checkbox_1, description = R.string.desc,
                    getter = { KPrefSample.check1 }, setter = { KPrefSample.check1 = it })
            checkbox(title = R.string.checkbox_2,
                    getter = { KPrefSample.check2 }, setter = { KPrefSample.check2 = it })
            checkbox(title = R.string.checkbox_3, enabled = false,
                    getter = { KPrefSample.check3 }, setter = { KPrefSample.check3 = it })
            colorPicker(title = R.string.text_color,
                    getter = { KPrefSample.textColor }, setter = { KPrefSample.textColor = it })
        }
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
