package ca.allanwang.kau.searchview

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.support.annotation.ColorInt
import android.support.annotation.IdRes
import android.support.annotation.StringRes
import android.support.transition.AutoTransition
import android.support.v7.widget.*
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import ca.allanwang.kau.R
import ca.allanwang.kau.kotlin.nonReadable
import ca.allanwang.kau.utils.*
import com.jakewharton.rxbinding2.widget.RxTextView
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.mikepenz.iconics.typeface.IIcon
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.runOnUiThread


/**
 * Created by Allan Wang on 2017-06-23.
 *
 * A materialized SearchView with complete theming and observables
 *
 * Huge thanks to @lapism for his base
 * https://github.com/lapism/SearchView
 */
class SearchView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    inner class Configs {
        var foregroundColor: Int
            get() = SearchItem.foregroundColor
            set(value) {
                if (SearchItem.foregroundColor == value) return
                SearchItem.foregroundColor = value
                tintForeground(value)
            }
        var backgroundColor: Int
            get() = SearchItem.backgroundColor
            set(value) {
                if (SearchItem.backgroundColor == value) return
                SearchItem.backgroundColor = value
                tintBackground(value)
            }
        var navIcon: IIcon? = GoogleMaterial.Icon.gmd_arrow_back
            set(value) {
                field = value
                iconNav.setSearchIcon(value)
                if (value == null) iconNav.gone()
            }
        /**
         * Optional icon just to the left of the clear icon
         * This is not implemented by default, but can be used for anything, such as mic or redirects
         * Make sure you add a click listener to [iconExtra] if you plan on using this
         */
        var extraIcon: IIcon? = null
            set(value) {
                field = value
                iconExtra.setSearchIcon(value)
                if (value == null) iconClear.gone()
            }
        var clearIcon: IIcon? = GoogleMaterial.Icon.gmd_clear
            set(value) {
                field = value
                iconClear.setSearchIcon(value)
                if (value == null) iconClear.gone()
            }
        var revealDuration: Long = 300L
        var transitionDuration: Long = 100L
        var shouldClearOnClose: Boolean = true
        var openListener: ((searchView: SearchView) -> Unit)? = null
        var closeListener: ((searchView: SearchView) -> Unit)? = null
        /**
         * Draw a divider between the search bar and the suggestion items
         * The divider is colored based on the foreground color
         */
        var withDivider: Boolean = true
            set(value) {
                field = value
                if (value) divider.visible() else divider.invisible()
            }
        var hintText: String?
            get() = editText.hint?.toString()
            set(value) {
                editText.hint = value
            }

        var hintTextRes: Int
            @Deprecated(level = DeprecationLevel.ERROR, message = "Non readable property")
            get() = nonReadable()
            @StringRes set(value) {
                hintText = context.string(value)
            }
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
         * Text watcher configurations on init
         * By default, the observable is on a separate thread, so you may directly execute background processes
         * This builder acts on an observable, so you may switch threads, debounce, and do anything else that you require
         */
        var textObserver: (observable: Observable<String>, searchView: SearchView) -> Unit = { _, _ -> }
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


    }

    /**
     * Contract for adapter items
     * Setting results will ensure that the values are sent on the UI thread
     */
    var results: List<SearchItem>
        get() = adapter.adapterItems
        set(value) = context.runOnUiThread {
            cardTransition()
            adapter.setNewList(
                    if (configs.noResultsFound > 0 && value.isEmpty())
                        listOf(SearchItem("", context.string(configs.noResultsFound), iicon = null))
                    else value)
        }

    /**
     * Empties the list on the UI thread
     * The noResults item will not be added
     */
    internal fun clearResults() = context.runOnUiThread { cardTransition(); adapter.clear() }

    val configs = Configs()
    //views
    private val shadow: View by bindView(R.id.search_shadow)
    private val card: CardView by bindView(R.id.search_cardview)
    private val iconNav: ImageView by bindView(R.id.search_nav)
    private val editText: AppCompatEditText by bindView(R.id.search_edit_text)
    val textEvents: Observable<String>
    private val progress: ProgressBar by bindView(R.id.search_progress)
    val iconExtra: ImageView by bindView(R.id.search_extra)
    private val iconClear: ImageView by bindView(R.id.search_clear)
    private val divider: View by bindView(R.id.search_divider)
    private val recycler: RecyclerView by bindView(R.id.search_recycler)
    val adapter = FastItemAdapter<SearchItem>()
    lateinit var parent: ViewGroup
    var menuItem: MenuItem? = null
    val isOpen: Boolean
        get() = card.isVisible()

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
        iconNav.setSearchIcon(configs.navIcon)
        iconClear.setSearchIcon(configs.clearIcon).setOnClickListener {
            editText.text.clear()
        }
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
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false //clear the fade between item changes
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
        textEvents = RxTextView.textChangeEvents(editText)
                .skipInitialValue()
                .observeOn(Schedulers.newThread())
                .map { it.text().toString().trim() }
        textEvents.filter { it.isBlank() }
                .subscribe { clearResults() }
    }

    internal fun ImageView.setSearchIcon(iicon: IIcon?): ImageView {
        setIcon(iicon, sizeDp = 18, color = configs.foregroundColor)
        return this
    }

    internal fun cardTransition(builder: AutoTransition.() -> Unit = {}) {
        card.transitionAuto { duration = configs.transitionDuration; builder() }
    }

    fun config(config: Configs.() -> Unit) {
        configs.config()
    }

    fun bind(parent: ViewGroup, menu: Menu, @IdRes id: Int, @ColorInt menuIconColor: Int = Color.WHITE, config: Configs.() -> Unit = {}): SearchView {
        config(config)
        configs.textObserver(textEvents.filter { it.isNotBlank() }, this)
        this.parent = parent
        menuItem = menu.findItem(id)
        if (menuItem!!.icon == null) menuItem!!.icon = GoogleMaterial.Icon.gmd_search.toDrawable(context, 18, menuIconColor)
        card.gone()
        menuItem!!.setOnMenuItemClickListener { configureCoords(it); revealOpen(); true }
        shadow.setOnClickListener { revealClose() }
        return this
    }

    fun unBind(replacementMenuItemClickListener: MenuItem.OnMenuItemClickListener? = null) {
        parent.removeView(this)
        menuItem?.setOnMenuItemClickListener(replacementMenuItemClickListener)
    }

    fun configureCoords(item: MenuItem) {
        val view = parent.findViewById<View>(item.itemId) ?: return
        val locations = IntArray(2)
        view.getLocationOnScreen(locations)
        menuX = (locations[0] + view.width / 2)
        menuHalfHeight = view.height / 2
        menuY = (locations[1] + menuHalfHeight)
        card.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                view.viewTreeObserver.removeOnPreDrawListener(this)
                val topAlignment = menuY - card.height / 2
                val params = (card.layoutParams as MarginLayoutParams).apply {
                    topMargin = topAlignment
                }
                card.layoutParams = params
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
        card.circularReveal(menuX, menuHalfHeight, duration = configs.revealDuration) {
            editText.showKeyboard()
            cardTransition()
            recycler.visible()
            shadow.fadeIn()
        }
    }

    fun revealClose() {
        if (!isOpen) return
        editText.hideKeyboard()
        shadow.fadeOut(duration = configs.transitionDuration)
        cardTransition {
            addEndListener {
                card.circularHide(menuX, menuHalfHeight, duration = configs.revealDuration,
                        onFinish = {
                            configs.closeListener?.invoke(this@SearchView)
                            if (configs.shouldClearOnClose) editText.text.clear()
                            recycler.gone()
                        })
            }
        }
        recycler.gone()
    }
}

fun ViewGroup.bindSearchView(menu: Menu, @IdRes id: Int, @ColorInt menuIconColor: Int = Color.WHITE, config: SearchView.Configs.() -> Unit = {}): SearchView {
    val searchView = SearchView(context)
    searchView.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
    addView(searchView)
    searchView.bind(this, menu, id, menuIconColor, config)
    return searchView
}
