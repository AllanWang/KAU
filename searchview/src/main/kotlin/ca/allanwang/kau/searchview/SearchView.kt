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
package ca.allanwang.kau.searchview

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.EditorInfo
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import ca.allanwang.kau.kotlin.Debouncer2
import ca.allanwang.kau.kotlin.debounce
import ca.allanwang.kau.logging.KL
import ca.allanwang.kau.searchview.SearchView.Configs
import ca.allanwang.kau.utils.INVALID_ID
import ca.allanwang.kau.utils.addEndListener
import ca.allanwang.kau.utils.adjustAlpha
import ca.allanwang.kau.utils.circularHide
import ca.allanwang.kau.utils.circularReveal
import ca.allanwang.kau.utils.fadeIn
import ca.allanwang.kau.utils.fadeOut
import ca.allanwang.kau.utils.gone
import ca.allanwang.kau.utils.goneIf
import ca.allanwang.kau.utils.hideKeyboard
import ca.allanwang.kau.utils.invisibleIf
import ca.allanwang.kau.utils.isVisible
import ca.allanwang.kau.utils.parentViewGroup
import ca.allanwang.kau.utils.runOnUiThread
import ca.allanwang.kau.utils.setIcon
import ca.allanwang.kau.utils.setMarginTop
import ca.allanwang.kau.utils.showKeyboard
import ca.allanwang.kau.utils.string
import ca.allanwang.kau.utils.tint
import ca.allanwang.kau.utils.toDrawable
import ca.allanwang.kau.utils.visible
import ca.allanwang.kau.utils.withLinearAdapter
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.mikepenz.fastadapter.select.selectExtension
import com.mikepenz.iconics.typeface.IIcon
import com.mikepenz.iconics.typeface.library.googlematerial.GoogleMaterial
import kotlinx.android.synthetic.main.kau_search_view.view.*

/**
 * Created by Allan Wang on 2017-06-23.
 *
 * A materialized SearchView with complete theming and customization
 * This view can be added programmatically and configured using the [Configs] DSL
 * It is preferred to add the view through an activity, but it can be attached to any ViewGroup
 * Beware of where specifically this is added, as its view or the keyboard may affect positioning
 *
 * Huge thanks to @lapism for his base
 * https://github.com/lapism/SearchView
 */
class SearchView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    /**
     * Collection of all possible arguments when building the SearchView
     * Everything is made as opened as possible, so additional components may be found in the [SearchView]
     * However, these are the main config options
     */
    class Configs {

        /**
         * The foreground color accounts for all text colors and icon colors
         * Various alpha levels may be used for sub texts/dividers etc
         */
        var foregroundColor: Int = SearchItem.foregroundColor

        /**
         * Namely the background for the card and recycler view
         */
        var backgroundColor: Int = SearchItem.backgroundColor

        /**
         * Icon for the leftmost ImageView, which typically contains the hamburger menu/back arror
         */
        var navIcon: IIcon? = GoogleMaterial.Icon.gmd_arrow_back

        /**
         * Optional icon just to the left of the clear icon
         * This is not implemented by default, but can be used for anything, such as mic or redirects
         * Returns the extra imageview
         * Set the iicon as null to hide the extra icon
         */
        var extraIcon: Pair<IIcon, OnClickListener>? = null

        /**
         * Icon for the rightmost ImageView, which typically contains a close icon
         */
        var clearIcon: IIcon? = GoogleMaterial.Icon.gmd_clear

        /**
         * Duration for the circular reveal animation
         */
        var revealDuration: Long = 300L

        /**
         * Duration for the auto transition, which is namely used to resize the recycler view
         */
        var transitionDuration: Long = 100L

        /**
         * Defines whether the edit text and mainAdapter should clear themselves when the searchView is closed
         */
        var shouldClearOnClose: Boolean = false

        /**
         * Callback that will be called every time the searchView opens
         */
        var openListener: ((searchView: SearchView) -> Unit)? = null

        /**
         * Callback that will be called every time the searchView closes
         */
        var closeListener: ((searchView: SearchView) -> Unit)? = null

        /**
         * Draw a divider between the search bar and the suggestion items
         * The divider is colored based on the [foregroundColor]
         */
        var withDivider: Boolean = true

        /**
         * Hint string to be set in the searchView
         */
        var hintText: String? = null

        /**
         * Hint string res to be set in the searchView
         */
        var hintTextRes: Int = INVALID_ID

        /**
         * StringRes for a "no results found" item
         * If [results] is ever set to an empty list, it will default to
         * a list with one item with this string
         *
         * For simplicity, kau contains [R.string.kau_no_results_found]
         * which you may use
         */
        var noResultsFound: Int = INVALID_ID

        /**
         * Callback for when the query changes
         * This callback does not run on the ui thread!
         * It is always on a worker thread, so there is no need for asynchronous calls
         * Likewise, calls modifying the UI should be passed through [runOnUiThread]
         */
        var textCallback: (query: String, searchView: SearchView) -> Unit = { _, _ -> }

        /**
         * Callback for when the query is changed to an empty string
         * Typically, this may be ignored as the adapter will simply be cleared,
         * but if we wish to do something else, we may pass a function
         * Returns [true] if the action was consumed, [false] otherwise (to execute default behaviour)
         */
        var textClearedCallback: (searchView: SearchView) -> Boolean = { _ -> false }

        /**
         * Callback for when the search action key is detected from the keyboard
         * Returns true if the searchbar should close afterwards, and false otherwise
         */
        var searchCallback: (query: String, searchView: SearchView) -> Boolean = { _, _ -> false }

        /**
         * Debouncing interval between callbacks
         */
        var textDebounceInterval: Long = 0

        /**
         * Click event for suggestion items
         * This event is only triggered when [key] is not blank (like in [noResultsFound]
         */
        var onItemClick: (position: Int, key: String, content: String, searchView: SearchView) -> Unit =
            { _, _, _, _ -> }

        /**
         * Long click event for suggestion items
         * This event is only triggered when [key] is not blank (like in [noResultsFound]
         */
        var onItemLongClick: (position: Int, key: String, content: String, searchView: SearchView) -> Unit =
            { _, _, _, _ -> }

        /**
         * If a [SearchItem]'s title contains the submitted query, make that portion bold
         * See [SearchItem.withHighlights]
         */
        var highlightQueryText: Boolean = true

        /**
         * Sets config attributes to the given searchView
         */
        internal fun apply(searchView: SearchView) {
            with(searchView) {
                if (SearchItem.foregroundColor != foregroundColor) {
                    SearchItem.foregroundColor = foregroundColor
                    tintForeground(foregroundColor)
                }
                if (SearchItem.backgroundColor != backgroundColor) {
                    SearchItem.backgroundColor = backgroundColor
                    tintBackground(backgroundColor)
                }
                val icons = mutableListOf(navIcon to kau_search_nav, clearIcon to kau_search_clear)
                val extra = extraIcon
                if (extra != null) {
                    icons.add(extra.first to kau_search_extra)
                }
                icons.forEach { (iicon, view) -> view.goneIf(iicon == null).setSearchIcon(iicon) }

                if (extra != null) {
                    kau_search_extra.setOnClickListener(extra.second)
                }
                kau_search_divider.invisibleIf(!withDivider)
                kau_search_edit_text.hint = context.string(hintTextRes, hintText)
                textCallback.terminate()
                textCallback = debounce(textDebounceInterval, this@Configs.textCallback)
            }
        }
    }

    /**
     * Contract for mainAdapter items
     * Setting results will ensure that the values are sent on the UI thread
     */
    var results: List<SearchItem>
        get() = adapter.adapterItems
        set(value) = context.runOnUiThread {
            val list = if (value.isEmpty() && configs.noResultsFound != INVALID_ID)
                listOf(SearchItem("", context.string(configs.noResultsFound), iicon = null))
            else value
            if (configs.highlightQueryText && value.isNotEmpty()) list.forEach {
                it.withHighlights(
                    kau_search_edit_text.text?.toString()
                )
            }
            cardTransition()
            adapter.setNewList(list)
        }

    /**
     * Empties the list on the UI thread
     * The noResults item will not be added
     */
    internal fun clearResults() {
        textCallback.cancel()
        context.runOnUiThread { cardTransition(); adapter.clear() }
    }

    private val configs = Configs()
    // views
    private var textCallback: Debouncer2<String, SearchView> =
        debounce(0) { query, _ -> KL.d { "Search query $query found; set your own textCallback" } }
    private val adapter = FastItemAdapter<SearchItem>()
    private var menuItem: MenuItem? = null
    val isOpen: Boolean
        get() = parent != null && kau_search_cardview.isVisible

    /**
     * The current text located in our searchview
     */
    val query: String
        get() = kau_search_edit_text.text?.toString()?.trim() ?: ""

    /*
     * Ripple start points and search view offset
     * These are calculated every time the search view is opened,
     * and can be overridden with the open listener if necessary
     */
    var menuX: Int = -1             //starting x for circular reveal
    var menuY: Int = -1             //reference for cardview's marginTop
    var menuHalfHeight: Int = -1    //starting y for circular reveal (relative to the cardview)

    init {
        View.inflate(context, R.layout.kau_search_view, this)
        z = 99f
        kau_search_nav.setSearchIcon(configs.navIcon).setOnClickListener { revealClose() }
        kau_search_clear.setSearchIcon(configs.clearIcon)
            .setOnClickListener { kau_search_edit_text.text?.clear() }
        tintForeground(configs.foregroundColor)
        tintBackground(configs.backgroundColor)
        with(kau_search_recycler) {
            isNestedScrollingEnabled = false
            withLinearAdapter(this@SearchView.adapter)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        hideKeyboard()
                    }
                }
            })
            itemAnimator = null
        }
        with(adapter) {
            selectExtension {
                isSelectable = true
            }
            onClickListener = { _, _, item, position ->
                if (item.key.isNotBlank()) configs.onItemClick(
                    position,
                    item.key,
                    item.content,
                    this@SearchView
                ); true
            }
            onLongClickListener = { _, _, item, position ->
                if (item.key.isNotBlank()) {
                    configs.onItemLongClick(
                        position,
                        item.key,
                        item.content,
                        this@SearchView
                    )
                }
                true
            }
        }
        kau_search_edit_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val text = s.toString().trim()
                textCallback.cancel()
                if (text.isNotEmpty()) {
                    textCallback(text, this@SearchView)
                } else if (!configs.textClearedCallback(this@SearchView)) {
                    clearResults()
                }
            }
        })
        kau_search_edit_text.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = kau_search_edit_text.text?.toString() ?: ""
                if (configs.searchCallback(query, this)) {
                    revealClose()
                } else {
                    kau_search_edit_text.hideKeyboard()
                }
                return@setOnEditorActionListener true
            }
            false
        }
    }

    internal fun ImageView.setSearchIcon(iicon: IIcon?): ImageView {
        setIcon(iicon, sizeDp = 18, color = configs.foregroundColor)
        return this
    }

    internal fun cardTransition(builder: TransitionSet.() -> Unit = {}) {
        TransitionManager.beginDelayedTransition(kau_search_cardview,
            //we are only using change bounds, as the recyclerview items may be animated as well,
            //which causes a measure IllegalStateException
            TransitionSet().addTransition(ChangeBounds()).apply {
                duration = configs.transitionDuration
                builder()
            })
    }

    /**
     * Update the base configurations and apply them to the searchView
     */
    fun config(config: Configs.() -> Unit) {
        configs.config()
        configs.apply(this)
    }

    /**
     * Binds the SearchView to a menu item and handles everything internally
     * This is assuming that SearchView has already been added to a ViewGroup
     * If not, see the extension function [bindSearchView]
     */
    fun bind(
        menu: Menu,
        @IdRes id: Int,
        @ColorInt menuIconColor: Int = Color.WHITE,
        config: Configs.() -> Unit = {}
    ): SearchView {
        config(config)
        val menuItem = menu.findItem(id)
            ?: throw IllegalArgumentException("Menu item with given id doesn't exist")
        if (menuItem.icon == null) {
            menuItem.icon = GoogleMaterial.Icon.gmd_search.toDrawable(context, 18, menuIconColor)
        }
        kau_search_cardview.gone()
        menuItem.setOnMenuItemClickListener { revealOpen(); true }
        kau_search_shadow.setOnClickListener { revealClose() }
        this.menuItem = menuItem
        return this
    }

    /**
     * Call to remove the searchView from the original menuItem,
     * with the option to replace the item click listener
     */
    fun unBind(replacementMenuItemClickListener: ((item: MenuItem) -> Boolean)? = null) {
        (parent as? ViewGroup)?.removeView(this)
        menuItem?.setOnMenuItemClickListener(replacementMenuItemClickListener)
        menuItem = null
    }

    private val locations = IntArray(2)

    private fun configureCoords(item: MenuItem?) {
        item ?: return
        if (parent !is ViewGroup) {
            return
        }
        val view = parentViewGroup.findViewById<View>(item.itemId) ?: return
        view.getLocationInWindow(locations)
        menuX = (locations[0] + view.width / 2)
        menuHalfHeight = view.height / 2
        menuY = (locations[1] + menuHalfHeight)
        kau_search_cardview.viewTreeObserver.addOnPreDrawListener(object :
            ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                view.viewTreeObserver.removeOnPreDrawListener(this)
                kau_search_cardview.setMarginTop(menuY - kau_search_cardview.height / 2)
                return true
            }
        })
    }

    /**
     * Handle a back press event
     * Returns true if back press is consumed, false otherwise
     */
    fun onBackPressed(): Boolean {
        if (isOpen && menuItem != null) {
            revealClose()
            return true
        }
        return false
    }

    /**
     * Tint foreground attributes
     * This can be done publicly through [configs], which will also save the color
     */
    internal fun tintForeground(@ColorInt color: Int) {
        kau_search_nav.drawable.setTint(color)
        kau_search_clear.drawable.setTint(color)
        kau_search_divider.setBackgroundColor(color.adjustAlpha(0.1f))
        kau_search_edit_text.tint(color)
        kau_search_edit_text.setTextColor(ColorStateList.valueOf(color))
        kau_search_edit_text.setHintTextColor(color.adjustAlpha(0.7f))
    }

    /**
     * Tint background attributes
     * This can be done publicly through [configs], which will also save the color
     */
    internal fun tintBackground(@ColorInt color: Int) {
        kau_search_cardview.setCardBackgroundColor(color)
    }

    fun revealOpen() {
        if (parent == null || isOpen) {
            return
        }
        context.runOnUiThread {
            /**
             * The y component is relative to the cardView, but it hasn't been drawn yet so its own height is 0
             * We therefore use half the menuItem height, which is a close approximation to our intended value
             * The cardView matches the parent's width, so menuX is correct
             */
            configureCoords(menuItem)
            configs.openListener?.invoke(this@SearchView)
            kau_search_shadow.fadeIn()
            kau_search_edit_text.showKeyboard()
            kau_search_cardview.circularReveal(
                menuX,
                menuHalfHeight,
                duration = configs.revealDuration
            ) {
                cardTransition()
                kau_search_recycler.visible()
            }
        }
    }

    fun revealClose() {
        if (parent == null || !isOpen) {
            return
        }
        context.runOnUiThread {
            kau_search_shadow.fadeOut(duration = configs.transitionDuration)
            cardTransition {
                addEndListener {
                    kau_search_cardview.circularHide(menuX,
                        menuHalfHeight,
                        duration = configs.revealDuration,
                        onFinish = {
                            configs.closeListener?.invoke(this@SearchView)
                            if (configs.shouldClearOnClose) {
                                kau_search_edit_text.text?.clear()
                            }
                        })
                }
            }
            kau_search_recycler.gone()
            kau_search_edit_text.hideKeyboard()
        }
    }
}

@DslMarker
annotation class KauSearch

/**
 * Helper function that binds to an activity's main view
 */
@KauSearch
fun Activity.bindSearchView(
    menu: Menu,
    @IdRes id: Int,
    @ColorInt menuIconColor: Int = Color.WHITE,
    config: Configs.() -> Unit = {}
): SearchView =
    findViewById<ViewGroup>(android.R.id.content).bindSearchView(menu, id, menuIconColor, config)

/**
 * Bind searchView to a menu item; call this in [Activity.onCreateOptionsMenu]
 * Be wary that if you may reinflate the menu many times (eg through [Activity.invalidateOptionsMenu]),
 * it may be worthwhile to hold a reference to the searchview and only bind it if it hasn't been bound before
 */
@KauSearch
fun ViewGroup.bindSearchView(
    menu: Menu,
    @IdRes id: Int,
    @ColorInt menuIconColor: Int = Color.WHITE,
    config: Configs.() -> Unit = {}
): SearchView {
    val searchView = SearchView(context)
    searchView.layoutParams =
        FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
    addView(searchView)
    searchView.bind(menu, id, menuIconColor, config)
    return searchView
}
