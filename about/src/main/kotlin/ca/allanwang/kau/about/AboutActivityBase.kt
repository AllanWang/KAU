/*
 * Copyright 2018 Allan Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ca.allanwang.kau.about

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import ca.allanwang.kau.adapters.FastItemThemedAdapter
import ca.allanwang.kau.adapters.ThemableIItemColors
import ca.allanwang.kau.adapters.ThemableIItemColorsDelegate
import ca.allanwang.kau.internal.KauBaseActivity
import ca.allanwang.kau.ui.widgets.ElasticDragDismissFrameLayout
import ca.allanwang.kau.utils.AnimHolder
import ca.allanwang.kau.utils.INVALID_ID
import ca.allanwang.kau.utils.dimenPixelSize
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.fastadapter.GenericItem
import kotlinx.android.synthetic.main.kau_activity_about.*

/**
 * Created by Allan Wang on 2017-06-28.
 *
 * Floating About Activity Panel for your app
 * This contains all the necessary layouts, and can be extended and configured using the [configBuilder]
 * The [rClass] is necessary to generate the list of libraries used in your app, and should point to your app's
 * R.string::class.java
 * If you don't need auto detect, you can pass null instead
 * Note that for the auto detection to work, the R fields must be excluded from Proguard
 * Manual lib listings and other extra modifications can be done so by overriding the open functions
 */
abstract class AboutActivityBase(val rClass: Class<*>?, private val configBuilder: Configs.() -> Unit = {}) :
    KauBaseActivity(), ViewPager.OnPageChangeListener {

    val currentPage: Int
        get() = about_pager.currentItem

    /**
     * Holds some common configurations that may be added directly from the constructor
     * Applied lazily since it needs the context to fetch resources
     */
    val configs: Configs by lazy { Configs().apply { configBuilder() } }

    /**
     * Holds that status of each page
     * 0 means nothing has happened
     * 1 means this page has been in view at least once
     * The rest is up to you
     */
    lateinit var pageStatus: IntArray

    val panels: List<AboutPanelContract> by lazy {
        val defaultPanels = mutableListOf(AboutPanelMain(), AboutPanelLibs())
        if (configs.faqXmlRes != INVALID_ID) {
            defaultPanels.add(AboutPanelFaqs())
        }
        defaultPanels
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.kau_activity_about)
        pageStatus = IntArray(panels.size)
        pageStatus[0] = 2 //the first page is instantly visible
        if (configs.textColor != null) {
            about_indicator.setColour(configs.textColor!!)
        }
        with(about_pager) {
            adapter = AboutPagerAdapter()
            pageMargin = dimenPixelSize(R.dimen.kau_spacing_normal)
            offscreenPageLimit = panels.size - 1
            addOnPageChangeListener(this@AboutActivityBase)
        }
        about_indicator.setViewPager(about_pager)
        about_draggable_frame.addListener(object : ElasticDragDismissFrameLayout.SystemChromeFader(this) {
            override fun onDragDismissed() {
                window.returnTransition = TransitionInflater.from(this@AboutActivityBase)
                    .inflateTransition(if (about_draggable_frame.translationY > 0) R.transition.kau_exit_slide_bottom else R.transition.kau_exit_slide_top)
                panels[currentPage].recycler?.stopScroll()
                finishAfterTransition()
            }
        })
        panels.forEachIndexed { index, contract -> contract.loadItems(this, index) }
    }

    class Configs : ThemableIItemColors by ThemableIItemColorsDelegate() {
        var cutoutTextRes: Int = INVALID_ID
        var cutoutText: String? = null
        var cutoutDrawableRes: Int = INVALID_ID
        var cutoutDrawable: Drawable? = null
        var cutoutForeground: Int? = null
        var libPageTitleRes: Int = R.string.kau_about_libraries_intro
        var libPageTitle: String? = null
            set(value) {
                field = value
                libPageTitleRes = INVALID_ID // reset res so we don't use our default
            }
        var faqXmlRes: Int = INVALID_ID
        var faqPageTitleRes: Int = R.string.kau_about_faq_intro
        var faqPageTitle: String? = null
            set(value) {
                field = value
                faqPageTitleRes = INVALID_ID // reset res so we don't use our default
            }
        /**
         * Whether new lines should be included
         */
        var faqParseNewLine: Boolean = true
    }

    /**
     * For [mainPanel]
     *
     * Open hook called just before the main page view is returned
     * Feel free to add your own items to the adapter in here
     */
    open fun postInflateMainPage(adapter: FastItemThemedAdapter<GenericItem>) {
    }

    /**
     * For [libPanel]
     *
     * Method to fetch the library list
     * This is fetched asynchronously and you may override it to customize the list
     */
    open fun getLibraries(libs: Libs): List<Library> =
        libs.prepareLibraries(this, null, null, true, true, true)!!

    /*
     * -------------------------------------------------------------------
     * Page 3: FAQ
     * -------------------------------------------------------------------
     */

    private inner class AboutPagerAdapter : PagerAdapter() {

        private val layoutInflater: LayoutInflater = LayoutInflater.from(this@AboutActivityBase)
        private val views = Array<View?>(panels.size) { null }

        override fun instantiateItem(collection: ViewGroup, position: Int): Any {
            val layout = getPage(position, collection)
            collection.addView(layout)
            return layout
        }

        override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
            collection.removeView(view as View)
            views[position] = null
        }

        override fun getCount(): Int = panels.size

        override fun isViewFromObject(view: View, `object`: Any): Boolean = view === `object`

        /**
         * Only get page if view does not exist
         */
        private fun getPage(position: Int, parent: ViewGroup): View {
            if (views[position] == null) views[position] = panels[position]
                .inflatePage(this@AboutActivityBase, parent, position)
            return views[position]!!
        }
    }

    override fun onPageScrollStateChanged(state: Int) = Unit

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) = Unit

    override fun onPageSelected(position: Int) {
        if (pageStatus[position] == 0) pageStatus[position] = 1 // mark as seen if previously null
        if (pageStatus[position] == 1) panels[position].addItems(this, position)
    }

    override fun onDestroy() {
        AnimHolder.decelerateInterpolator.invalidate() // clear the reference to the interpolators we've used
        super.onDestroy()
    }
}
