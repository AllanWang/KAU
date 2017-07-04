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
import ca.allanwang.kau.adapters.FastItemThemedAdapter
import ca.allanwang.kau.adapters.ThemableIItemColors
import ca.allanwang.kau.adapters.ThemableIItemColorsDelegate
import ca.allanwang.kau.animators.FadeScaleAnimator
import ca.allanwang.kau.iitems.CutoutIItem
import ca.allanwang.kau.iitems.HeaderIItem
import ca.allanwang.kau.iitems.LibraryIItem
import ca.allanwang.kau.utils.*
import ca.allanwang.kau.widgets.ElasticDragDismissFrameLayout
import ca.allanwang.kau.widgets.InkPageIndicator
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.fastadapter.IItem
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
abstract class AboutActivityBase(val rClass: Class<*>, val configBuilder: Configs.() -> Unit = {}) : AppCompatActivity(), ViewPager.OnPageChangeListener {

    val draggableFrame: ElasticDragDismissFrameLayout by bindView(R.id.about_draggable_frame)
    val pager: ViewPager by bindView(R.id.about_pager)
    val indicator: InkPageIndicator by bindView(R.id.about_indicator)
    /**
     * Holds some common configurations that may be added directly from the constructor
     * Applied lazily since it needs the context to fetch resources
     */
    val configs: Configs by lazy { Configs().apply { configBuilder() } }
    /**
     * Number of pages in the adapter
     * Defaults to just the main view and lib view
     */
    open val pageCount: Int = 2
    /**
     * Page position for the libs
     * This is generated automatically if [inflateLibPage] is called
     */
    private var libPage: Int = -2
    /**
     * Holds that status of each page
     * 0 means nothing has happened
     * 1 means this page has been in view at least once
     * The rest is up to you
     */
    lateinit var pageStatus: IntArray
    /**
     * Holds the lib items once they are fetched asynchronously
     */
    var libItems: List<LibraryIItem>? = null
    /**
     * Holds the adapter for the library page; this is generated later because it uses the config colors
     */
    lateinit var libAdapter: FastItemThemedAdapter<IItem<*, *>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.kau_activity_about)
        pageStatus = IntArray(pageCount)
        libAdapter = FastItemThemedAdapter(configs)
        LibraryIItem.bindClickEvents(libAdapter)
        if (configs.textColor != null) indicator.setColour(configs.textColor!!)
        with(pager) {
            adapter = AboutPagerAdapter()
            pageMargin = dimenPixelSize(R.dimen.kau_spacing_normal)
            addOnPageChangeListener(this@AboutActivityBase)
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

    inner class Configs : ThemableIItemColors by ThemableIItemColorsDelegate() {
        var cutoutTextRes: Int = -1
        var cutoutText: String? = null
        var cutoutDrawableRes: Int = -1
        var cutoutDrawable: Drawable? = null
        var cutoutForeground: Int? = null
        var libPageTitleRes: Int = -1
        var libPageTitle: String? = string(R.string.kau_about_libraries_intro) //This is in the string by default since it's lower priority
        /**
         * Transition to be called if the view is dragged down
         */
        var transitionExitReversed: Int = R.transition.kau_about_return_downward
    }

    /**
     * Method to fetch the library list
     * This is fetched asynchronously and you may override it to customize the list
     */
    open fun getLibraries(libs: Libs): List<Library> = libs.prepareLibraries(this, null, null, true, true)

    /**
     * Gets the view associated with the given page position
     * Keep in mind that when inflating, do NOT add the view to the viewgroup
     * Use layoutInflater.inflate(id, parent, false)
     */
    open fun getPage(position: Int, layoutInflater: LayoutInflater, parent: ViewGroup): View {
        return when (position) {
            0 -> inflateMainPage(layoutInflater, parent, position)
            pageCount - 1 -> inflateLibPage(layoutInflater, parent, position)
            else -> throw InvalidParameterException()
        }
    }

    /**
     * Create the main view with the cutout
     */
    open fun inflateMainPage(layoutInflater: LayoutInflater, parent: ViewGroup, position: Int): View {
        val fastAdapter = FastItemThemedAdapter<IItem<*, *>>(configs)
        val recycler = fullLinearRecycler(fastAdapter)
        fastAdapter.add(CutoutIItem {
            with(configs) {
                text = string(cutoutTextRes, cutoutText)
                drawable = drawable(cutoutDrawableRes, cutoutDrawable)
                if (configs.cutoutForeground != null) foregroundColor = configs.cutoutForeground!!
            }
        }.apply {
            themeEnabled = configs.cutoutForeground == null
        })
        postInflateMainPage(fastAdapter)
        return recycler
    }

    /**
     * Open hook called just before the main page view is returned
     * Feel free to add your own items to the adapter in here
     */
    open fun postInflateMainPage(adapter: FastItemThemedAdapter<IItem<*, *>>) {

    }

    /**
     * Create the lib view with the list of libraries
     */
    open fun inflateLibPage(layoutInflater: LayoutInflater, parent: ViewGroup, position: Int): View {
        libPage = position
        val v = layoutInflater.inflate(R.layout.kau_recycler_detached_background, parent, false)
        val recycler = v.findViewById<RecyclerView>(R.id.kau_recycler_detached)
        recycler.adapter = libAdapter
        recycler.itemAnimator = FadeScaleAnimator(itemDelayFactor = 0.2f).apply { addDuration = 300; interpolator = AnimHolder.decelerateInterpolator(this@AboutActivityBase) }
        val background = v.findViewById<View>(R.id.kau_recycler_detached_background)
        if (configs.backgroundColor != null) background.setBackgroundColor(configs.backgroundColor!!.colorToForeground())
        doAsync {
            libItems = getLibraries(Libs(this@AboutActivityBase, Libs.toStringArray(rClass.fields))).map { LibraryIItem(it) }
            if (libPage >= 0 && pageStatus[libPage] == 1)
                uiThread { addLibItems() }
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

        /**
         * Only get page if view does not exist
         */
        private fun getPage(position: Int, parent: ViewGroup): View {
            if (views[position] == null) views[position] = getPage(position, layoutInflater, parent)
            return views[position]!!
        }
    }

    override fun onPageScrollStateChanged(state: Int) {}

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    override fun onPageSelected(position: Int) {
        if (pageStatus[position] == 0) pageStatus[position] = 1 // mark as seen if previously null
        if (position == libPage && libItems != null && pageStatus[position] == 1) {
            pageStatus[position] = 2            //add libs and mark as such
            postDelayed(300) { addLibItems() }  //delay so that the animations occur once the page is fully switched
        }
    }

    /**
     * Function that is called when the view is ready to add the lib items
     * Feel free to add your own items here
     */
    open fun addLibItems() {
        libAdapter.add(HeaderIItem(text = configs.libPageTitle, textRes = configs.libPageTitleRes))
                .add(libItems)
    }

    override fun onDestroy() {
        AnimHolder.decelerateInterpolator.invalidate() //clear the reference to the interpolators we've used
        super.onDestroy()
    }
}