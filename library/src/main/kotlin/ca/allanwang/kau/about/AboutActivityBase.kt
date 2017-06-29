package ca.allanwang.kau.about

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.allanwang.kau.R
import ca.allanwang.kau.iitems.CutoutIItem
import ca.allanwang.kau.iitems.LibraryIItem
import ca.allanwang.kau.utils.*
import ca.allanwang.kau.widgets.ElasticDragDismissFrameLayout
import ca.allanwang.kau.widgets.InkPageIndicator
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.fastadapter.IItem
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
        var cutoutText: String? = null
        var cutoutDrawableRes: Int = -1
        var cutoutDrawable: Drawable? = null
        var mainPageTitleRes: Int = -1
        var mainPageTitle: String = "Kau test"
        var libPageTitleRes: Int = -1
        var libPageTitle: String? = string(R.string.kau_about_libraries_intro)
        var transitionExitReversed: Int = R.transition.kau_about_return_downward
    }

    open fun getLibraries(libs: Libs): List<Library> = libs.prepareLibraries(this, null, null, true, true)

    open val pageCount: Int = 2

    /**
     * Gets the view associated with the given page position
     * Keep in mind that when inflating, do NOT add the view to the viewgroup
     * Use layoutInflater.inflate(id, parent, false)
     */
    open fun getPage(position: Int, layoutInflater: LayoutInflater, parent: ViewGroup): View {
        return when (position) {
            0 -> inflateMainPage(layoutInflater, parent)
            pageCount - 1 -> inflateLibPage(layoutInflater, parent)
            else -> throw InvalidParameterException()
        }
    }

    open fun inflateMainPage(layoutInflater: LayoutInflater, parent: ViewGroup): View {
        val fastAdapter = FastItemAdapter<IItem<*, *>>()
        val recycler = fullLinearRecycler {
            adapter = fastAdapter
        }
        fastAdapter.add(CutoutIItem {
            with(configs) {
                text = string(cutoutTextRes, cutoutText)
                drawable = drawable(cutoutDrawableRes, cutoutDrawable)
            }
        })
        return recycler
    }


    open fun inflateLibPage(layoutInflater: LayoutInflater, parent: ViewGroup): View {
        val v = layoutInflater.inflate(R.layout.kau_recycler_detached_background, parent, false)
        val fastAdapter = FastItemAdapter<IItem<*, *>>()
        val recycler = v.findViewById<RecyclerView>(R.id.kau_recycler_detached)
        recycler.adapter = fastAdapter
        val background = v.findViewById<View>(R.id.kau_recycler_detached_background)
        doAsync {
            val libs = getLibraries(Libs(this@AboutActivityBase, Libs.toStringArray(rClass.fields))).map { LibraryIItem(it) }
            uiThread {
                recycler.transitionDelayed(R.transition.kau_enter_slide_top) //TODO fix this
                fastAdapter.add(libs)
            }
        }
        return v
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