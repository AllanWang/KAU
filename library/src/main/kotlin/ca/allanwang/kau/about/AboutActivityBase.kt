package ca.allanwang.kau.about

import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import ca.allanwang.kau.R
import ca.allanwang.kau.utils.bindView
import ca.allanwang.kau.utils.dimenPixelSize
import ca.allanwang.kau.utils.string
import ca.allanwang.kau.views.CutoutTextView
import ca.allanwang.kau.widgets.ElasticDragDismissFrameLayout
import ca.allanwang.kau.widgets.InkPageIndicator
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.security.InvalidParameterException

/**
 * Created by Allan Wang on 2017-06-28.
 *
 * Floating About Activity Panel for your app
 * This contains all the necessary layouts, and can be extended and configured using the [configBuilder]
 * The [rClass] is necessary to generate the list of libraries used in your app, and should point to your app's
 * R.string::class.java
 * Note that for the auto detection to work, the R fields must be excluded from Proguard
 * Manual lib listings and other extra modifications can be done so by overriding the open functions
 */
abstract class AboutActivityBase(val rClass: Class<*>, val configBuilder: Configs.() -> Unit = {}) : AppCompatActivity() {

    val draggableFrame: ElasticDragDismissFrameLayout by bindView(R.id.about_draggable_frame)
    val pager: ViewPager by bindView(R.id.about_pager)
    val indicator: InkPageIndicator by bindView(R.id.about_indicator)
    val configs: Configs by lazy { Configs().apply { configBuilder() } }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.kau_activity_about)
        with(pager) {
            adapter = AboutPagerAdapter()
            pageMargin = dimenPixelSize(R.dimen.kau_spacing_normal)
        }
        indicator.setViewPager(pager)
        draggableFrame.addListener(object : ElasticDragDismissFrameLayout.SystemChromeFader(this) {
            override fun onDragDismissed() {
                // if we drag dismiss downward then the default reversal of the enter
                // transition would slide content upward which looks weird. So reverse it.
                if (draggableFrame.translationY > 0) {
                    window.returnTransition = TransitionInflater.from(this@AboutActivityBase)
                            .inflateTransition(configs.transitionExitReversed)
                }
                finishAfterTransition()
            }
        })
    }

    inner class Configs {
        var cutoutTextRes: Int = -1
        val cutoutText: String? = "KAU" //todo make null
        var mainPageTitleRes: Int = -1
        var mainPageTitle: String = "Kau test"
        var libPageTitleRes: Int = -1
        var libPageTitle: String? = string(R.string.kau_about_libraries_intro)
        var transitionExitReversed: Int = R.transition.kau_about_return_downward
    }

    open fun getLibraries(libs: Libs): List<Library> = libs.prepareLibraries(this, null, null, true, true)

    open val pageCount: Int = 2

    open fun getPage(position: Int, layoutInflater: LayoutInflater, parent: ViewGroup): View {
        return when (position) {
            0 -> inflateMainPage(layoutInflater, parent)
            pageCount - 1 -> inflateLibPage(layoutInflater, parent)
            else -> throw InvalidParameterException()
        }
    }

    fun inflateMainPage(layoutInflater: LayoutInflater, parent: ViewGroup): View {
        val v = layoutInflater.inflate(R.layout.kau_about_section_main, parent, false)
        postInflateMainPage(
                v.findViewById<CutoutTextView>(R.id.about_main_cutout),
                v.findViewById<FrameLayout>(R.id.about_main_bottom_container),
                v.findViewById<TextView>(R.id.about_main_bottom_text)
        )
        return v
    }

    open fun postInflateMainPage(cutout: CutoutTextView, bottomContainer: FrameLayout, bottomText: TextView) {
        with (configs) {
            cutout.text = string(cutoutTextRes, cutoutText)
            bottomText.text = string(mainPageTitleRes, mainPageTitle)
        }
    }

    fun inflateLibPage(layoutInflater: LayoutInflater, parent: ViewGroup): View {
        val v = layoutInflater.inflate(R.layout.kau_about_section_libraries, parent, false)
        postInflateLibPage(
                v.findViewById<TextView>(R.id.about_library_title),
                v.findViewById<RecyclerView>(R.id.about_library_recycler)
        )
        return v
    }

    open fun postInflateLibPage(title: TextView, recycler: RecyclerView) {
        title.text = string(configs.libPageTitleRes, configs.libPageTitle)
        val libAdapter = FastItemAdapter<LibraryItem>()
        with(recycler) {
            layoutManager = LinearLayoutManager(this@AboutActivityBase)
            adapter = libAdapter
        }
        doAsync {
            val libs = getLibraries(Libs(this@AboutActivityBase, Libs.toStringArray(rClass.fields))).map { LibraryItem(it) }
            uiThread { libAdapter.add(libs) }
        }
    }

    inner class AboutPagerAdapter : PagerAdapter() {

        private val layoutInflater: LayoutInflater = LayoutInflater.from(this@AboutActivityBase)
        private val views = Array<View?>(pageCount) { null }

        override fun instantiateItem(collection: ViewGroup, position: Int): Any {
            val layout = getPage(position, collection)
            collection.addView(layout)
            return layout
        }

        override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
            collection.removeView(view as View)
            views[position] = null
        }

        override fun getCount(): Int = pageCount

        override fun isViewFromObject(view: View, `object`: Any): Boolean = view === `object`

        private fun getPage(position: Int, parent: ViewGroup): View {
            if (views[position] == null) views[position] = getPage(position, layoutInflater, parent)
            return views[position]!!
        }
    }
}