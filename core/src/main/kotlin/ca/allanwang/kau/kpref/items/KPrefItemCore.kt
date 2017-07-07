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
import ca.allanwang.kau.R
import ca.allanwang.kau.adapters.ThemableIItem
import ca.allanwang.kau.adapters.ThemableIItemDelegate
import ca.allanwang.kau.kpref.GlobalOptions
import ca.allanwang.kau.kpref.KPrefMarker
import ca.allanwang.kau.utils.*
import com.mikepenz.fastadapter.items.AbstractItem
import com.mikepenz.iconics.typeface.IIcon

/**
 * Created by Allan Wang on 2017-06-05.
 *
 * Core class containing nothing but the view items
 */

abstract class KPrefItemCore(val core: CoreContract) : AbstractItem<KPrefItemCore, KPrefItemCore.ViewHolder>(),
        ThemableIItem by ThemableIItemDelegate() {

    override final fun getViewHolder(v: View) = ViewHolder(v)

    @CallSuper
    override fun bindView(viewHolder: ViewHolder, payloads: List<Any>) {
        super.bindView(viewHolder, payloads)
        with(viewHolder) {
            val context = itemView.context
            title.text = context.string(core.titleRes)
            if (core.descRes > 0)
                desc?.visible()?.setText(core.descRes)
            else
                desc?.gone()
            if (core.iicon != null) icon?.visible()?.setIcon(core.iicon, 24)
            else icon?.gone()
            innerFrame?.removeAllViews()
            val textColor = core.globalOptions.textColor?.invoke()
            if (textColor != null) {
                title.setTextColor(textColor)
                desc?.setTextColor(textColor)
            }
            val accentColor = core.globalOptions.accentColor?.invoke()
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
            innerFrame?.removeAllViews()
            lowerFrame?.removeAllViews()
        }
    }

    /**
     * Core values for all kpref items
     */
    @KPrefMarker
    interface CoreContract {
        val globalOptions: GlobalOptions
        @get:StringRes val titleRes: Int
        var descRes: Int
            @StringRes get
        var iicon: IIcon?

        /**
         * Attempts to reload current item by identifying it with its [titleRes]
         */
        fun reloadSelf()
    }

    /**
     * Default implementation of [CoreContract]
     */
    class CoreBuilder(override val globalOptions: GlobalOptions,
                      override @param:StringRes val titleRes: Int) : CoreContract {
        override var descRes: Int = -1
        override var iicon: IIcon? = null

        override fun reloadSelf() {
            globalOptions.reloadByTitle(titleRes)
        }
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val title: TextView by bindView(R.id.kau_pref_title)
        val container: ViewGroup? by bindOptionalView(R.id.kau_pref_container)
        val desc: TextView? by bindOptionalView(R.id.kau_pref_desc)
        val icon: ImageView? by bindOptionalView(R.id.kau_pref_icon)
        val innerFrame: LinearLayout? by bindOptionalView(R.id.kau_pref_inner_frame)
        val lowerFrame: LinearLayout? by bindOptionalView(R.id.kau_pref_lower_frame)
        val innerContent: View?
            get() = itemView.findViewById(R.id.kau_pref_inner_content)
        val lowerContent: View?
            get() = itemView.findViewById(R.id.kau_pref_lower_content)

        inline fun <reified T : View> bindInnerView(@LayoutRes id: Int) = bindInnerView(id) { _: T -> }

        inline fun <reified T : View> bindInnerView(@LayoutRes id: Int, onFirstBind: (T) -> Unit): T {
            if (innerFrame == null) throw IllegalStateException("Cannot bind inner view when innerFrame does not exist")
            if (innerContent !is T) {
                innerFrame!!.removeAllViews()
                LayoutInflater.from(innerFrame!!.context).inflate(id, innerFrame)
                onFirstBind(innerContent as T)
            }
            return innerContent as T
        }

        inline fun <reified T : View> bindLowerView(@LayoutRes id: Int) = bindLowerView(id) { _: T -> }

        inline fun <reified T : View> bindLowerView(@LayoutRes id: Int, onFirstBind: (T) -> Unit): T {
            if (lowerFrame == null) throw IllegalStateException("Cannot bind inner view when lowerContent does not exist")
            if (lowerContent !is T) {
                lowerFrame!!.removeAllViews()
                LayoutInflater.from(lowerFrame!!.context).inflate(id, lowerFrame)
                onFirstBind(lowerContent as T)
            }
            return lowerContent as T
        }

        operator fun get(@IdRes id: Int): View = itemView.findViewById(id)
    }
}