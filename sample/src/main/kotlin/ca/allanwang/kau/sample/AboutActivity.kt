package ca.allanwang.kau.sample

import android.os.Bundle
import android.os.PersistableBundle
import ca.allanwang.kau.about.AboutActivityBase
import ca.allanwang.kau.about.MainItem
import ca.allanwang.kau.utils.string
import com.mikepenz.fastadapter.adapters.HeaderAdapter

/**
 * Created by Allan Wang on 2017-06-27.
 */
class AboutActivity : AboutActivityBase(R.string::class.java) {

    val mainHeader = HeaderAdapter<MainItem>()

//    override fun onCreateSections(): List<Pair<String, HeaderAdapter<*>>> = listOf(
//            String.format(string(R.string.kau_about_x), "KAU") to mainHeader
//    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        mainHeader.add(MainItem {
//            title = string(R.string.app_name)
//            author = "Allan Wang"
//            version = BuildConfig.VERSION_NAME
//        })
    }

    override fun onPostCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onPostCreate(savedInstanceState, persistentState)
        recycler.adapter.notifyDataSetChanged()
    }
}