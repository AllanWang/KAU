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

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ca.allanwang.kau.adapters.FastItemThemedAdapter
import ca.allanwang.kau.animators.FadeScaleAnimatorAdd
import ca.allanwang.kau.animators.KauAnimator
import ca.allanwang.kau.animators.NoAnimatorChange
import ca.allanwang.kau.iitems.HeaderIItem
import ca.allanwang.kau.utils.AnimHolder
import ca.allanwang.kau.utils.KAU_BOTTOM
import ca.allanwang.kau.utils.colorToForeground
import ca.allanwang.kau.utils.drawable
import ca.allanwang.kau.utils.fullLinearRecycler
import ca.allanwang.kau.utils.postDelayed
import ca.allanwang.kau.utils.string
import ca.allanwang.kau.utils.withMarginDecoration
import ca.allanwang.kau.xml.kauParseFaq
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.fastadapter.GenericItem
import java.lang.reflect.Field
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by Allan Wang on 2017-08-02.
 *
 * The core logic for pages used in [AboutActivityBase]
 */
interface AboutPanelContract {
    /**
     * Model list to be added to [adapter]
     */
    var items: List<GenericItem>
    /**
     * The adapter, will be late initialized as it depends on configs
     */
    var adapter: FastItemThemedAdapter<GenericItem>
    /**
     * Reference to the recyclerview, will be used to stop scrolling upon exit
     */
    var recycler: RecyclerView?

    /**
     * The base inflation method that will be called for new pages from the page adapter
     * Keep in mind that when inflating, do NOT add the view to the viewgroup
     * Use layoutInflater.inflate(id, parent, false)
     */
    fun inflatePage(activity: AboutActivityBase, parent: ViewGroup, position: Int): View

    /**
     * Convenience method called during [inflatePage]
     * No return value necessary
     */
    fun onInflatingPage(activity: AboutActivityBase, recycler: RecyclerView, position: Int)

    /**
     * Triggers start of item loading
     * Typically called with [inflatePage]
     */
    fun loadItems(activity: AboutActivityBase, position: Int)

    /**
     * Called when the  [adapter] should take in the items
     * This typically happens once the user has scroll to the page,
     * so they may see a transition
     *
     * [AboutActivityBase.pageStatus] should be updated accordingly,
     * as triggering this does not necessarily mean that the items are added
     */
    fun addItems(activity: AboutActivityBase, position: Int)
}

abstract class AboutPanelRecycler : AboutPanelContract {

    override var items: List<GenericItem> = emptyList()

    override lateinit var adapter: FastItemThemedAdapter<GenericItem>

    override var recycler: RecyclerView? = null

    override fun onInflatingPage(
        activity: AboutActivityBase,
        recycler: RecyclerView,
        position: Int
    ) {
        recycler.adapter = adapter
        recycler.itemAnimator = KauAnimator(
            addAnimator = FadeScaleAnimatorAdd(scaleFactor = 0.7f, itemDelayFactor = 0.2f),
            changeAnimator = NoAnimatorChange
        ).apply {
            addDuration = 300; interpolator = AnimHolder.decelerateInterpolator(recycler.context)
        }
    }

    override fun inflatePage(activity: AboutActivityBase, parent: ViewGroup, position: Int): View {
        val v = LayoutInflater.from(activity)
            .inflate(R.layout.kau_recycler_detached_background, parent, false)
        adapter = FastItemThemedAdapter(activity.configs)
        recycler = v.findViewById(R.id.kau_recycler_detached)
        onInflatingPage(activity, recycler!!, position)
        val background = v.findViewById<View>(R.id.kau_recycler_detached_background)
        if (activity.configs.backgroundColor != null) background.setBackgroundColor(activity.configs.backgroundColor!!.colorToForeground())
        loadItems(activity, position)
        return v
    }

    override fun addItems(activity: AboutActivityBase, position: Int) {
        if (items.isEmpty()) {
            return
        }
        activity.pageStatus[position] = 2
        postDelayed(300) { addItemsImpl(activity, position) }
    }

    abstract fun addItemsImpl(activity: AboutActivityBase, position: Int)
}

/**
 * Panel delegate for the main page
 * The loading is synchronous, so it is done on inflation
 * All other loading and adding methods are overridden to do nothing
 * There is a [AboutActivityBase.postInflateMainPage] hook that can be overridden
 * to update this panel without extending the whole delegate
 */
open class AboutPanelMain : AboutPanelRecycler() {

    override fun onInflatingPage(
        activity: AboutActivityBase,
        recycler: RecyclerView,
        position: Int
    ) {
    }

    override fun inflatePage(activity: AboutActivityBase, parent: ViewGroup, position: Int): View {
        with(activity) {
            adapter = FastItemThemedAdapter(configs)
            recycler = fullLinearRecycler(adapter)
            adapter.add(CutoutIItem {
                with(configs) {
                    text = string(cutoutTextRes, cutoutText)
                    drawable = drawable(cutoutDrawableRes, cutoutDrawable)
                    if (configs.cutoutForeground != null) foregroundColor =
                        configs.cutoutForeground!!
                }
            }.apply {
                themeEnabled = configs.cutoutForeground == null
            })
            postInflateMainPage(adapter)
            return recycler!!
        }
    }

    override fun loadItems(activity: AboutActivityBase, position: Int) {}
    override fun addItems(activity: AboutActivityBase, position: Int) {
        activity.pageStatus[position] = 2
    }

    override fun addItemsImpl(activity: AboutActivityBase, position: Int) {}
}

/**
 * Panel for loading libraries
 * There is a [AboutActivityBase.getLibraries] hook that can be overridden
 * to customize the libraries listed
 */
open class AboutPanelLibs : AboutPanelRecycler() {

    override fun onInflatingPage(
        activity: AboutActivityBase,
        recycler: RecyclerView,
        position: Int
    ) {
        super.onInflatingPage(activity, recycler, position)
        recycler.withMarginDecoration(16, KAU_BOTTOM)
        LibraryIItem.bindEvents(adapter)
    }

    override fun loadItems(activity: AboutActivityBase, position: Int) {
        with(activity) {
            launch {
                items = withContext(Dispatchers.Default) {
                    getLibraries(
                        if (rClass == null) Libs(activity)
                        else Libs(activity, rClass.fields.toStringArray())
                    ).map(::LibraryIItem)
                }
                if (pageStatus[position] == 1)
                    addItems(activity, position)
            }
        }
    }

    // TODO remove and use from AboutLibrary
    // https://github.com/mikepenz/AboutLibraries/issues/449
    private fun Array<Field>.toStringArray(): Array<String> =
        map { it.name }.filter { it.contains("define_") }.toTypedArray()

    override fun addItemsImpl(activity: AboutActivityBase, position: Int) {
        with(activity.configs) {
            adapter.add(HeaderIItem(text = libPageTitle, textRes = libPageTitleRes))
                .add(items)
        }
    }
}

open class AboutPanelFaqs : AboutPanelRecycler() {

    override fun onInflatingPage(
        activity: AboutActivityBase,
        recycler: RecyclerView,
        position: Int
    ) {
        super.onInflatingPage(activity, recycler, position)
        FaqIItem.bindEvents(adapter)
    }

    override fun loadItems(activity: AboutActivityBase, position: Int) {
        with(activity) {
            launch {
                items = withContext(Dispatchers.IO) {
                    kauParseFaq(
                        configs.faqXmlRes,
                        configs.faqParseNewLine
                    )
                }.map(::FaqIItem)
                if (pageStatus[position] == 1)
                    addItems(activity, position)
            }
        }
    }

    override fun addItemsImpl(activity: AboutActivityBase, position: Int) {
        with(activity.configs) {
            adapter.add(HeaderIItem(text = faqPageTitle, textRes = faqPageTitleRes))
                .add(items)
        }
    }
}
