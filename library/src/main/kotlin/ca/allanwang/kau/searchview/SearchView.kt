package ca.allanwang.kau.searchview

import android.content.Context
import android.support.annotation.ColorInt
import android.support.annotation.IdRes
import android.support.v7.widget.AppCompatEditText
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import ca.allanwang.kau.R
import ca.allanwang.kau.logging.KL
import ca.allanwang.kau.utils.*
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.mikepenz.iconics.typeface.IIcon


/**
 * Created by Allan Wang on 2017-06-23.
 */
class SearchView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    //configs
    inner class Configs {
        var foregroundColor: Int = 0xdd000000.toInt()
            set(value) {
                if (field == value) return
                field = value
                tintForeground(value)
            }
        var backgroundColor: Int = 0xfffafafa.toInt()
            set(value) {
                if (field == value) return
                field = value
                tintBackground(value)
            }
        var navIcon: IIcon? = GoogleMaterial.Icon.gmd_arrow_back
            set(value) {
                field = value
                iconNav.setSearchIcon(value)
                if (value == null) iconNav.gone()
            }
        var micIcon: IIcon? = GoogleMaterial.Icon.gmd_mic
            set(value) {
                field = value
                iconMic.setSearchIcon(value)
                if (value == null) iconMic.gone()
            }
        var clearIcon: IIcon? = GoogleMaterial.Icon.gmd_clear
            set(value) {
                field = value
                iconClear.setSearchIcon(value)
                if (value == null) iconClear.gone()
            }
        var revealDuration: Long = 300L
        var shouldClearOnOpen: Boolean = true
        var openListener: ((searchView: SearchView) -> Unit)? = null
        var closeListener: ((searchView: SearchView) -> Unit)? = null
    }

    val configs = Configs()
    //views
    val shadow: View by bindView(R.id.search_shadow)
    val card: CardView by bindView(R.id.search_cardview)
    val iconNav: ImageView by bindView(R.id.search_nav)
    val editText: AppCompatEditText by bindView(R.id.search_edit_text)
    val progress: ProgressBar by bindView(R.id.search_progress)
    val iconMic: ImageView by bindView(R.id.search_mic)
    val iconClear: ImageView by bindView(R.id.search_clear)
    val recycler: RecyclerView by bindView(R.id.search_recycler)
    val adapter = FastItemAdapter<SearchItem>()
    lateinit var parent: ViewGroup
    val isOpen: Boolean
        get() = card.isVisible()

    //menu view
    var menuX: Int = -1
    var menuY: Int = -1
    var menuHalfHeight: Int = -1

    init {
        View.inflate(context, R.layout.kau_search_view, this)
        iconNav.setSearchIcon(configs.navIcon)
        iconMic.setSearchIcon(configs.micIcon)
        iconClear.setSearchIcon(configs.clearIcon)
        tintForeground(configs.foregroundColor)
        tintBackground(configs.backgroundColor)
        with(recycler) {
            layoutManager = LinearLayoutManager(context)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_DRAGGING) hideKeyboard()
                }
            })
            adapter = this@SearchView.adapter
        }
    }

    internal fun ImageView.setSearchIcon(iicon: IIcon?) {
        setIcon(iicon, sizeDp = 18, color = configs.foregroundColor)
    }

    fun config(config: Configs.() -> Unit) {
        configs.config()
    }

    fun bind(parent: ViewGroup, menu: Menu, @IdRes id: Int, config: Configs.() -> Unit = {}): SearchView {
        config(config)
        this.parent = parent
        val item = menu.findItem(id)
        if (item.icon == null) item.icon = GoogleMaterial.Icon.gmd_search.toDrawable(context, 20)
        card.gone()
        item.setOnMenuItemClickListener { configureCoords(it); revealOpen(); true }
        shadow.setOnClickListener { revealClose() }
        return this
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
                with(card) {
                    KL.e("S $width $measuredWidth $height $measuredHeight")
                }
                val topAlignment = menuY - card.height / 2
                val params = (card.layoutParams as MarginLayoutParams).apply {
                    topMargin = topAlignment
                }
                card.layoutParams = params
                return false
            }
        })
    }

    fun tintForeground(@ColorInt color: Int) {
        iconNav.drawable.setTint(color)
        iconMic.drawable.setTint(color)
        iconClear.drawable.setTint(color)
        SearchItem.foregroundColor = color
    }

    fun tintBackground(@ColorInt color: Int) {
        card.setCardBackgroundColor(color)
    }

    fun revealOpen() {
        if (isOpen) return
        /**
         * The y component is relative to the cardView, but it hasn't been drawn yet so its own height is 0
         * We therefore use half the menuItem height, which is a close approximation to our intended value
         * The cardView matches the parent's width, so menuX is correct
         */
        card.circularReveal(menuX, menuHalfHeight, duration = configs.revealDuration,
                onStart = {
                    configs.openListener?.invoke(this)
                    if (configs.shouldClearOnOpen) editText.text.clear()
                },
                onFinish = {
                    editText.requestFocus()
                    shadow.fadeIn()
                })
    }

    fun revealClose() {
        if (!isOpen) return
        shadow.fadeOut() {
            card.circularHide(menuX, menuHalfHeight, duration = configs.revealDuration,
                    onFinish = {
                        configs.closeListener?.invoke(this)
                    })
        }
    }
}

fun ViewGroup.bindSearchView(menu: Menu, @IdRes id: Int, config: SearchView.Configs.() -> Unit = {}): SearchView {
    val searchView = SearchView(context)
    searchView.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
    addView(searchView)
    searchView.bind(this, menu, id, config)
    return searchView
}
