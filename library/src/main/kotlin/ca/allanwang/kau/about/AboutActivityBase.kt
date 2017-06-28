package ca.allanwang.kau.about

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import ca.allanwang.kau.R
import ca.allanwang.kau.adapters.ChainedAdapters
import ca.allanwang.kau.logging.KL
import ca.allanwang.kau.utils.bindView
import ca.allanwang.kau.utils.string
import ca.allanwang.kau.views.KauTextSlider
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.fastadapter.adapters.HeaderAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * Created by Allan Wang on 2017-06-26.
 *
 * Customizable About Activity
 * Will automatically include a section containing all of the libraries registered under About Libraries
 * Takes in [rClass], which is the R.string::class.java for your app
 * It is used to get the libs dynamically
 * Make sure to add the following if you are using proguard:
 * # About library
 *      -keep class .R
 *      -keep class **.R$* {
 *      <fields>;
 * }
 *
 */
open class AboutActivityBase(val rClass: Class<*>) : AppCompatActivity() {

    val toolbar: Toolbar by bindView(R.id.kau_toolbar)
    val toolbarText: KauTextSlider by bindView(R.id.kau_toolbar_text)
    val recycler: RecyclerView by bindView(R.id.kau_recycler)
    val libSection: Pair<String, HeaderAdapter<LibraryItem>> by lazy { string(R.string.kau_dependencies_used) to HeaderAdapter<LibraryItem>() }
    val sectionsChain: ChainedAdapters<String> = ChainedAdapters()

    fun addLibsAsync() {
        doAsync {
            val libs = Libs(this@AboutActivityBase, Libs.toStringArray(rClass.fields))
            val items = getLibraries(libs)
            uiThread {
                KL.e("Coun ${items.count()}")
                libSection.second.add(items.map { LibraryItem(it) })
            }
        }
    }

    /**
     * By default, the libraries will be extracted dynamically and sorted
     * Override this to define your own list
     */
    open fun getLibraries(libs: Libs): List<Library> = libs.prepareLibraries(this@AboutActivityBase, null, null, true, true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.kau_activity_about)
        val sections = onCreateSections()
        sectionsChain.add(sections)
        if (!sections.contains(libSection)) sectionsChain.add(libSection)
        sectionsChain.bindRecyclerView(recycler) {
            item, _, dy ->
            if (dy > 0) toolbarText.setNextText(item)
            else toolbarText.setPrevText()
        }
        toolbarText.setCurrentText(sectionsChain[0].first)
    }

    override fun onPostCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onPostCreate(savedInstanceState, persistentState)
        addLibsAsync()
    }

    /**
     * Get all the header adapters
     * The adapters should be listed in the order that they appear,
     * and if the [libSection] shouldn't be at the end, it should be added in this list
     */
    open fun onCreateSections(): List<Pair<String, HeaderAdapter<*>>> = listOf(libSection)
}