package ca.allanwang.kau.searchview

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.support.annotation.ColorInt
import android.support.annotation.IdRes
import android.support.transition.ChangeBounds
import android.support.transition.TransitionManager
import android.support.transition.TransitionSet
import android.support.v7.widget.AppCompatEditText
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import ca.allanwang.kau.kotlin.Debouncer2
import ca.allanwang.kau.kotlin.debounce
import ca.allanwang.kau.logging.KL
import ca.allanwang.kau.searchview.SearchView.Configs
import ca.allanwang.kau.ui.views.BoundedCardView
import ca.allanwang.kau.utils.*
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.mikepenz.iconics.typeface.IIcon
import org.jetbrains.anko.runOnUiThread


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
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
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
        var hintTextRes: Int = -1
        /**
         * StringRes for a "no results found" item
         * If [results] is ever set to an empty list, it will default to
         * a list with one item with this string
         *
         * For simplicity, kau contains [R.string.kau_no_results_found]
         * which you may use
         */
        var noResultsFound: Int = -1
        /**
         * Callback for when the query changes
         */
        var textCallback: (query: String, searchView: SearchView) -> Unit = { _, _ -> }
        /**
         * Debouncing interval between callbacks
         */
        var textDebounceInterval: Long = 0
        /**
         * Click event for suggestion items
         * This event is only triggered when [key] is not blank (like in [noResultsFound]
         */
        var onItemClick: (position: Int, key: String, content: String, searchView: SearchView) -> Unit = { _, _, _, _ -> }
        /**
         * Long click event for suggestion items
         * This event is only triggered when [key] is not blank (like in [noResultsFound]
         */
        var onItemLongClick: (position: Int, key: String, content: String, searchView: SearchView) -> Unit = { _, _, _, _ -> }
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
                    tintForeground(backgroundColor)
                }
                val icons = mutableListOf(navIcon to iconNav, clearIcon to iconClear)
                val extra = extraIcon
                if (extra != null) icons.add(extra.first to iconExtra)
                icons.forEach { (iicon, view) -> view.goneIf(iicon == null).setSearchIcon(iicon) }

                if (extra != null) iconExtra.setOnClickListener(extra.second)
                divider.invisibleIf(!withDivider)
                editText.hint = context.string(hintTextRes, hintText)
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
            val list = if (value.isEmpty() && configs.noResultsFound > 0)
                listOf(SearchItem("", context.string(configs.noResultsFound), iicon = null))
            else value
            if (configs.highlightQueryText && value.isNotEmpty()) list.forEach { it.withHighlights(editText.text.toString()) }
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

    val configs = Configs()
    //views
    private val shadow: View by bindView(R.id.kau_search_shadow)
    private val card: BoundedCardView by bindView(R.id.kau_search_cardview)
    private val iconNav: ImageView by bindView(R.id.kau_search_nav)
    private val editText: AppCompatEditText by bindView(R.id.kau_search_edit_text)
    private val progress: ProgressBar by bindView(R.id.kau_search_progress)
    private val iconExtra: ImageView by bindView(R.id.kau_search_extra)
    private val iconClear: ImageView by bindView(R.id.kau_search_clear)
    private val divider: View by bindView(R.id.kau_search_divider)
    private val recycler: RecyclerView by bindView(R.id.kau_search_recycler)
    private var textCallback: Debouncer2<String, SearchView>
            = debounce(0) { query, _ -> KL.d("Search query $query found; set your own textCallback") }
    val adapter = FastItemAdapter<SearchItem>()
    var menuItem: MenuItem? = null
    val isOpen: Boolean
        get() = card.isVisible

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
        iconNav.setSearchIcon(configs.navIcon).setOnClickListener { revealClose() }
        iconClear.setSearchIcon(configs.clearIcon).setOnClickListener { editText.text.clear() }
        tintForeground(configs.foregroundColor)
        tintBackground(configs.backgroundColor)
        with(recycler) {
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(context)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_DRAGGING) hideKeyboard()
                }
            })
            adapter = this@SearchView.adapter
            itemAnimator = null
        }
        with(adapter) {
            withSelectable(true)
            withOnClickListener { _, _, item, position ->
                if (item.key.isNotBlank()) configs.onItemClick(position, item.key, item.content, this@SearchView); true
            }
            withOnLongClickListener { _, _, item, position ->
                if (item.key.isNotBlank()) configs.onItemLongClick(position, item.key, item.content, this@SearchView); true
            }
        }
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val valid = !s.isNullOrBlank()
                if (valid) textCallback(s.toString().trim(), this@SearchView)
                else clearResults()
            }

        })
    }

    internal fun ImageView.setSearchIcon(iicon: IIcon?): ImageView {
        setIcon(iicon, sizeDp = 18, color = configs.foregroundColor)
        return this
    }

    internal fun cardTransition(builder: TransitionSet.() -> Unit = {}) {
        TransitionManager.beginDelayedTransition(card,
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
    fun bind(menu: Menu, @IdRes id: Int, @ColorInt menuIconColor: Int = Color.WHITE, config: Configs.() -> Unit = {}): SearchView {
        config(config)
        menuItem = menu.findItem(id) ?: throw IllegalArgumentException("Menu item with given id doesn't exist")
        if (menuItem!!.icon == null) menuItem!!.icon = GoogleMaterial.Icon.gmd_search.toDrawable(context, 18, menuIconColor)
        card.gone()
        menuItem!!.setOnMenuItemClickListener { configureCoords(it); revealOpen(); true }
        shadow.setOnClickListener { revealClose() }
        return this
    }

    /**
     * Call to remove the searchView from the original menuItem,
     * with the option to replace the item click listener
     */
    fun unBind(replacementMenuItemClickListener: ((item: MenuItem) -> Boolean)? = null) {
        parentViewGroup.removeView(this)
        menuItem?.setOnMenuItemClickListener(replacementMenuItemClickListener)
        menuItem = null
    }

    private fun configureCoords(item: MenuItem) {
        val view = parentViewGroup.findViewById<View>(item.itemId) ?: return
        val locations = IntArray(2)
        view.getLocationOnScreen(locations)
        menuX = (locations[0] + view.width / 2)
        menuHalfHeight = view.height / 2
        menuY = (locations[1] + menuHalfHeight)
        card.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                view.viewTreeObserver.removeOnPreDrawListener(this)
                card.setMarginTop(menuY - card.height / 2)
                return false
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
        iconNav.drawable.setTint(color)
        iconClear.drawable.setTint(color)
        divider.setBackgroundColor(color.adjustAlpha(0.1f))
        editText.tint(color)
        editText.setTextColor(ColorStateList.valueOf(color))
    }

    /**
     * Tint background attributes
     * This can be done publicly through [configs], which will also save the color
     */
    internal fun tintBackground(@ColorInt color: Int) {
        card.setCardBackgroundColor(color)
    }

    fun revealOpen() {
        if (isOpen) return
        /**
         * The y component is relative to the cardView, but it hasn't been drawn yet so its own height is 0
         * We therefore use half the menuItem height, which is a close approximation to our intended value
         * The cardView matches the parent's width, so menuX is correct
         */
        configs.openListener?.invoke(this)
        shadow.fadeIn()
        editText.showKeyboard()
        card.circularReveal(menuX, menuHalfHeight, duration = configs.revealDuration) {
            cardTransition()
            recycler.visible()
        }
    }

    fun revealClose() {
        if (!isOpen) return
        shadow.fadeOut(duration = configs.transitionDuration)
        cardTransition {
            addEndListener {
                card.circularHide(menuX, menuHalfHeight, duration = configs.revealDuration,
                        onFinish = {
                            configs.closeListener?.invoke(this@SearchView)
                            if (configs.shouldClearOnClose) editText.text.clear()
                        })
            }
        }
        recycler.gone()
        editText.hideKeyboard()
    }
}

@DslMarker
annotation class KauSearch

/**
 * Helper function that binds to an activity's main view
 */
@KauSearch
fun Activity.bindSearchView(menu: Menu, @IdRes id: Int, @ColorInt menuIconColor: Int = Color.WHITE, config: SearchView.Configs.() -> Unit = {}): SearchView
        = findViewById<ViewGroup>(android.R.id.content).bindSearchView(menu, id, menuIconColor, config)

/**
 * Bind searchView to a menu item; call this in [Activity.onCreateOptionsMenu]
 * Be wary that if you may reinflate the menu many times (eg through [Activity.invalidateOptionsMenu]),
 * it may be worthwhile to hold a reference to the searchview and only bind it if it hasn't been bound before
 */
@KauSearch
fun ViewGroup.bindSearchView(menu: Menu, @IdRes id: Int, @ColorInt menuIconColor: Int = Color.WHITE, config: SearchView.Configs.() -> Unit = {}): SearchView {
    val searchView = SearchView(context)
    searchView.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
    addView(searchView)
    searchView.bind(menu, id, menuIconColor, config)
    return searchView
}

