package ca.allanwang.kau.kpref.items

import android.support.annotation.CallSuper
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import butterknife.ButterKnife
import ca.allanwang.kau.R
import ca.allanwang.kau.kpref.KPrefAdapterBuilder
import ca.allanwang.kau.utils.*
import com.mikepenz.fastadapter.items.AbstractItem
import com.mikepenz.iconics.typeface.IIcon

/**
 * Created by Allan Wang on 2017-06-05.
 *
 * Core class containing nothing but the view items
 */

abstract class KPrefItemCore(val builder: KPrefAdapterBuilder,
                             @StringRes val title: Int,
                             @StringRes val description: Int = -1,
                             val iicon: IIcon? = null) : AbstractItem<KPrefItemCore, KPrefItemCore.ViewHolder>() {

    override final fun getViewHolder(v: View) = ViewHolder(v)

    @CallSuper
    override fun bindView(viewHolder: ViewHolder, payloads: List<Any>) {
        super.bindView(viewHolder, payloads)
        with(viewHolder) {
            val context = itemView.context
            title.text = context.string(this@KPrefItemCore.title)
            if (description > 0)
                desc?.visible()?.setText(description)
            else
                desc?.gone()
            if (iicon != null) {
                iconFrame?.visible()
                icon?.setIcon(iicon, 48)
            } else iconFrame?.gone()
            innerFrame?.removeAllViews()
            onPostBindView(this)
            setColors(this, builder)
        }
    }

    @CallSuper
    open fun setColors(viewHolder: ViewHolder, builder: KPrefAdapterBuilder) {
        with(viewHolder) {
            if (builder.textColor != null) {
                title.setTextColor(builder.textColor!!)
                desc?.setTextColor(builder.textColor!!)
            }
            if (builder.accentColor != null) {
                icon?.drawable?.setTint(builder.accentColor!!)
            }
        }
    }

    abstract fun onPostBindView(viewHolder: ViewHolder)

    abstract fun onClick(itemView: View): Boolean

    override fun unbindView(holder: ViewHolder) {
        super.unbindView(holder)
        with(holder) {
            title.text = null
            desc?.text = null
            icon?.setImageDrawable(null)
            innerFrame?.removeAllViews()
            itemView.isEnabled = true
            itemView.alpha = 1.0f
        }
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val title: TextView by bindView(R.id.kau_pref_title)
        val desc: TextView? by bindOptionalView(R.id.kau_pref_desc)
        val icon: ImageView? by bindOptionalView(R.id.kau_pref_icon)
        val iconFrame: LinearLayout? by bindOptionalView(R.id.kau_pref_icon_frame)
        val innerFrame: LinearLayout? by bindOptionalView(R.id.kau_pref_inner_frame)

        init {
            ButterKnife.bind(v)
        }

        fun addInnerView(@LayoutRes id: Int) {
            LayoutInflater.from(innerFrame!!.context).inflate(id, innerFrame)
        }

        operator fun get(@IdRes id: Int): View = itemView.findViewById(id)
    }
}