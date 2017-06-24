package ca.allanwang.kau.searchview

import android.content.Context
import android.support.annotation.ColorInt
import android.support.annotation.IdRes
import android.support.v7.widget.AppCompatEditText
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
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

    companion object {
        fun bind(parent: ViewGroup, menu: Menu, @IdRes id: Int, config: Configs.() -> Unit = {}) {
            SearchView(parent.context).apply {
                layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
                parent.addView(this)
                this.bind(parent, menu, id, config)
            }
        }
    }

    //configs
    inner class Configs {
        var foregroundColor: Int = 0xddffffff.toInt()
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
                iconNav.setIcon(value)
                if (value == null) iconNav.gone()
            }
        var micIcon: IIcon? = GoogleMaterial.Icon.gmd_mic
            set(value) {
                field = value
                iconMic.setIcon(value)
                if (value == null) iconMic.gone()
            }
        var clearIcon: IIcon? = GoogleMaterial.Icon.gmd_clear
            set(value) {
                field = value
                iconClear.setIcon(value)
                if (value == null) iconClear.gone()
            }
        var revealDuration: Long = 300L
        var shouldClearOnOpen: Boolean = true
        var openListener: (() -> Unit)? = null
        var closeListener: (() -> Unit)? = null
    }

    val configs = Configs()
    //views
    private val shadow: View by bindView(R.id.search_shadow)
    private val card: CardView by bindView(R.id.search_cardview)
    private val iconNav: ImageView by bindView(R.id.search_nav)
    private val editText: AppCompatEditText by bindView(R.id.search_edit_text)
    private val progress: ProgressBar by bindView(R.id.search_progress)
    private val iconMic: ImageView by bindView(R.id.search_mic)
    private val iconClear: ImageView by bindView(R.id.search_clear)
    private val recycler: RecyclerView by bindView(R.id.search_recycler)
    private val adapter = FastItemAdapter<SearchItem>()
    private lateinit var parent: ViewGroup
    val isOpen: Boolean
        get() = card.isVisible()

    //menu view
    private var revealX: Int = -1
    private var revealY: Int = -1

    init {
        View.inflate(context, R.layout.kau_search_view, this)
        iconNav.setIcon(configs.navIcon)
        iconMic.setIcon(configs.micIcon)
        iconClear.setIcon(configs.clearIcon)
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

    internal fun ImageView.setSearchIcon(iicon: IIcon) {
        setIcon(iicon, sizeDp = 20, color = configs.foregroundColor)
    }

    fun config(config: Configs.() -> Unit) {
        configs.config()
    }

    fun bind(parent: ViewGroup, menu: Menu, @IdRes id: Int, config: Configs.() -> Unit = {}) {
        config(config)
        this.parent = parent
        val item = menu.findItem(id)
        if (item.icon == null) item.icon = GoogleMaterial.Icon.gmd_search.toDrawable(context, 20)
        card.gone()
        item.setOnMenuItemClickListener { configureCoords(it); revealOpen(); true }
        shadow.setOnClickListener { revealClose() }
    }

    fun configureCoords(item: MenuItem) {
        val view = parent.findViewById<View>(item.itemId) ?: return
        val locations = IntArray(2)
        view.getLocationOnScreen(locations)
        revealX = (locations[0] + view.width / 2)
        revealY = (locations[1] + view.height / 2)
        val topAlignment = revealY - card.height / 2
        val params = (card.layoutParams as MarginLayoutParams).apply {
            topMargin = topAlignment
        }
        card.layoutParams = params
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
        card.circularReveal(revealX, revealY, duration = configs.revealDuration,
                onStart = {
                    configs.openListener?.invoke()
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
            card.circularHide(revealX, revealY, duration = configs.revealDuration,
                    onFinish = {
                        configs.closeListener?.invoke()
                    })
        }
    }
}