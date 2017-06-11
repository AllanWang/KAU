package ca.allanwang.kau.kpref.items

import android.support.annotation.CallSuper
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import butterknife.ButterKnife
import ca.allanwang.kau.R
import ca.allanwang.kau.kpref.KPrefAdapterBuilder
import ca.allanwang.kau.logging.SL
import ca.allanwang.kau.utils.*
import com.mikepenz.fastadapter.items.AbstractItem
import com.mikepenz.iconics.typeface.IIcon
import java.util.*

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
            if (iicon != null) icon?.visible()?.setIcon(iicon, 48)
            else icon?.gone()
            innerFrame?.removeAllViews()
            val textColor = builder.textColor?.invoke()
            if (textColor != null) {
                title.setTextColor(textColor)
                desc?.setTextColor(textColor)
            }
            val accentColor = builder.accentColor?.invoke()
            if (accentColor != null) {
                icon?.drawable?.setTint(accentColor)
            }
            onPostBindView(this, textColor, accentColor)
        }
    }

    abstract fun onPostBindView(viewHolder: ViewHolder, textColor: Int?, accentColor: Int?)

    abstract fun onClick(itemView: View, innerContent: View?): Boolean

    override fun unbindView(holder: ViewHolder) {
        super.unbindView(holder)
        with(holder) {
            title.text = null
            desc?.text = null
            icon?.setImageDrawable(null)
//            innerFrame?.removeAllViews()
        }
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val title: TextView by bindView(R.id.kau_pref_title)
        val container: ViewGroup? by bindOptionalView(R.id.kau_pref_container)
        val desc: TextView? by bindOptionalView(R.id.kau_pref_desc)
        val icon: ImageView? by bindOptionalView(R.id.kau_pref_icon)
        val innerFrame: LinearLayout? by bindOptionalView(R.id.kau_pref_inner_frame)
        val innerContent: View?
            get() = itemView.findViewById(R.id.kau_pref_inner_content)

        init {
            ButterKnife.bind(v)
        }

        inline fun <reified T : View> bindInnerView(@LayoutRes id: Int): T {
            if (innerFrame == null) throw IllegalStateException("Cannot bind inner view when innerFrame does not exist")
            if (innerContent !is T) {
                innerFrame!!.removeAllViews()
                LayoutInflater.from(innerFrame!!.context).inflate(id, innerFrame)
            } else {
                SL.d("Inner view still attached")
            }
            return innerContent as T
        }

        inline fun <reified T : View> getInnerView() = innerContent as T

        operator fun get(@IdRes id: Int): View = itemView.findViewById(id)
    }
}