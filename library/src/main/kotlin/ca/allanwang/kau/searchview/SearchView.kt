package ca.allanwang.kau.searchview

import android.content.Context
import android.support.annotation.ColorInt
import android.support.annotation.IdRes
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.Menu
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import ca.allanwang.kau.R
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
    var foregroundColor: Int = 0xddffffff.toInt()
        set(value) {
            if (field == value) return
            field = value
            tintForeground(value)
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

    //views
    private val shadow: View by bindView(R.id.search_shadow)
    private val card: CardView by bindView(R.id.search_shadow)
    private val iconNav: ImageView by bindView(R.id.search_nav)
    //TODO edittext
    private val progress: ProgressBar by bindView(R.id.search_progress)
    private val iconMic: ImageView by bindView(R.id.search_mic)
    private val iconClear: ImageView by bindView(R.id.search_clear)
    private val recycler: RecyclerView by bindView(R.id.search_recycler)
    private val adapter = FastItemAdapter<SearchItem>()

    init {
        View.inflate(context, R.layout.kau_search_view, this)
        iconNav.setIcon(navIcon)
        iconMic.setIcon(micIcon)
        iconClear.setIcon(clearIcon)
        tintForeground(foregroundColor)
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

    fun config(action: SearchView.() -> Unit) = action()

    fun bind(menu: Menu, @IdRes id: Int) {
        val item = menu.findItem(id)
        if (item.icon == null) item.icon = GoogleMaterial.Icon.gmd_search.toDrawable(context, 20)
        gone()
    }

    fun tintForeground(@ColorInt color: Int) {
        iconNav.drawable.setTint(color)
        iconMic.drawable.setTint(color)
        iconClear.drawable.setTint(color)
        SearchItem.foregroundColor = color
    }
}