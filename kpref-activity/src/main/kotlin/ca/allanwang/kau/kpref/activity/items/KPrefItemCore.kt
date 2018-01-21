package ca.allanwang.kau.kpref.activity.items

import android.annotation.SuppressLint
import android.support.annotation.CallSuper
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import ca.allanwang.kau.adapters.ThemableIItem
import ca.allanwang.kau.adapters.ThemableIItemDelegate
import ca.allanwang.kau.kpref.activity.GlobalOptions
import ca.allanwang.kau.kpref.activity.KPrefMarker
import ca.allanwang.kau.kpref.activity.R
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

    final override fun getViewHolder(v: View) = ViewHolder(v)

    protected fun ViewHolder.updateDesc() {
        val descRes = core.descFun()
        if (descRes > 0)
            desc?.visible()?.setText(descRes)
        else
            desc?.gone()
    }

    protected fun ViewHolder.updateTitle() {
        title.setText(core.titleFun())
    }

    /**
     * NewApi is suppressed as [buildIsLollipopAndUp] already covers it
     */
    @SuppressLint("NewApi")
    @CallSuper
    override fun bindView(holder: ViewHolder, payloads: List<Any>) {
        super.bindView(holder, payloads)
        with(holder) {
            updateTitle()
            updateDesc()
            if (core.iicon != null) icon?.visible()?.setIcon(core.iicon, 24)
            else icon?.gone()
            innerFrame?.removeAllViews()
            withTextColor {
                title.setTextColor(it)
                desc?.setTextColor(it.adjustAlpha(0.65f))
            }
            if (buildIsLollipopAndUp)
                withAccentColor {
                    icon?.drawable?.setTint(it)
                }
        }
    }

    protected inline fun withAccentColor(action: (color: Int) -> Unit) =
            withColor(core.globalOptions.accentColor, action)

    protected inline fun withTextColor(action: (color: Int) -> Unit) =
            withColor(core.globalOptions.textColor, action)

    protected inline fun withColor(noinline supplier: (() -> Int)?,
                                   action: (color: Int) -> Unit) {
        val color = supplier?.invoke() ?: return
        action(color)
    }

    open fun onClick(itemView: View) = Unit

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
        val titleId: Int
        var titleFun: () -> Int
        var descRes: Int
        var descFun: () -> Int
        var iicon: IIcon?
        var visible: () -> Boolean

        /**
         * Attempts to reload current item by identifying it with its [id]
         */
        fun reloadSelf()
    }

    /**
     * Default implementation of [CoreContract]
     */
    class CoreBuilder(override val globalOptions: GlobalOptions,
                      override val titleId: Int) : CoreContract {
        override var descRes: Int = -1
            set(value) {
                field = value
                descFun = { field }
            }
        override var descFun = { -1 }
        override var iicon: IIcon? = null
        override var visible = { true }
        override var titleFun = { titleId }

        override fun reloadSelf() {
            globalOptions.reloadByTitle(titleId)
        }
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val title: TextView by bindView(R.id.kau_pref_title)
        val container: ViewGroup? by bindOptionalView(R.id.kau_pref_container)
        val desc: TextView? by bindOptionalView(R.id.kau_pref_desc)
        val icon: ImageView? by bindOptionalView(R.id.kau_pref_icon)
        val innerFrame: LinearLayout? by bindOptionalView(R.id.kau_pref_inner_frame)
        val lowerFrame: LinearLayout? by bindOptionalView(R.id.kau_pref_lower_frame)
        val innerView: View?
            get() = itemView.findViewById(R.id.kau_pref_inner_content)
        val lowerContent: View?
            get() = itemView.findViewById(R.id.kau_pref_lower_content)

        inline fun <reified T : View> bindInnerView(@LayoutRes id: Int) = bindInnerView(id) { _: T -> }

        inline fun <reified T : View> bindInnerView(@LayoutRes id: Int, onFirstBind: (T) -> Unit): T {
            val innerFrame = this.innerFrame
                    ?: throw IllegalStateException("Cannot bind inner view when innerFrame does not exist")
            if (innerView !is T) {
                innerFrame.removeAllViews()
                LayoutInflater.from(innerFrame.context).inflate(id, innerFrame)
                onFirstBind(innerView as T)
            }
            return innerView as T
        }

        inline fun <reified T : View> bindLowerView(@LayoutRes id: Int) = bindLowerView(id) { _: T -> }

        inline fun <reified T : View> bindLowerView(@LayoutRes id: Int, onFirstBind: (T) -> Unit): T {
            val lowerFrame = this.lowerFrame
                    ?: throw IllegalStateException("Cannot bind inner view when lowerContent does not exist")
            if (lowerContent !is T) {
                lowerFrame.removeAllViews()
                LayoutInflater.from(lowerFrame.context).inflate(id, lowerFrame)
                onFirstBind(lowerContent as T)
            }
            return lowerContent as T
        }

        operator fun get(@IdRes id: Int): View = itemView.findViewById(id)
    }
}