package ca.allanwang.kau.sample

import android.os.Bundle
import android.os.Handler
import ca.allanwang.kau.about.AboutActivityBase
import ca.allanwang.kau.about.MainItem
import ca.allanwang.kau.adapters.SectionAdapter
import ca.allanwang.kau.logging.KL
import ca.allanwang.kau.utils.string

/**
 * Created by Allan Wang on 2017-06-27.
 */
class AboutActivity : AboutActivityBase(R.string::class.java) {

    val mainHeader = SectionAdapter<MainItem>()

    override fun onCreateSections(): List<Pair<String, SectionAdapter<*>>> = listOf(
            //            libSection,
            String.format(string(R.string.kau_about_x), "KAU") to mainHeader
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sectionsChain.baseAdapter.withPositionBasedStateManagement(false)
    }

    override fun onPostCreate() {
        Handler().postDelayed({
            mainHeader.add(MainItem {
                title = string(R.string.app_name)
                author = "Allan Wang"
                version = BuildConfig.VERSION_NAME
            })
            mainHeader.add(MainItem {
                title = string(R.string.app_name)
                author = "Allan Wang"
                version = BuildConfig.VERSION_NAME
            })
            mainHeader.add(MainItem { })
            mainHeader.add(MainItem { })
            KL.e(R.layout.kau_about_item_library.toString())
            KL.e(R.layout.kau_about_item_main.toString())
            KL.e(R.id.kau_item_about_library.toString())
            KL.e(R.id.kau_item_about_main.toString())
//            KL.e(sectionsChain.baseAdapter.getItemViewType(0).toString())
//            KL.e(sectionsChain.baseAdapter.getItemViewType(6).toString())
            with(sectionsChain.baseAdapter) {
                KL.e(mainHeader.adapterItemCount.toString())
                KL.e(getAdapter(1)?.getGlobalPosition(0)?.toString() ?: "")
                KL.e((getItem(0) is MainItem).toString())
                KL.e((getItem(6) is MainItem).toString())
//                KL.e((getItem(6) is MainItem).toString())
            }
        }, 2000)
    }
}