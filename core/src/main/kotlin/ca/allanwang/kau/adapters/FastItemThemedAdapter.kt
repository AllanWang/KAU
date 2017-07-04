package ca.allanwang.kau.adapters

import android.content.res.ColorStateList
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import ca.allanwang.kau.utils.adjustAlpha
import ca.allanwang.kau.views.createSimpleRippleDrawable
import com.mikepenz.fastadapter.IExpandable
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.ISubItem
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter

/**
 * Created by Allan Wang on 2017-06-29.
 *
 * Adapter with a set of colors that will be added to all subsequent items
 * Changing a color while the adapter is not empty will reload all items
 *
 * This adapter overrides every method where an item is added
 * If that item extends [ThemableIItem], then the colors will be set
 */
class FastItemThemedAdapter<Item : IItem<*, *>>(
        textColor: Int? = null,
        backgroundColor: Int? = null,
        accentColor: Int? = null
) : FastItemAdapter<Item>() {
    constructor(colors: ThemableIItemColors) : this(colors.textColor, colors.backgroundColor, colors.accentColor)

    var textColor: Int? = textColor
        set(value) {
            if (field == value) return
            field = value
            themeChanged()
        }
    var backgroundColor: Int? = backgroundColor
        set(value) {
            if (field == value) return
            field = value
            themeChanged()
        }
    var accentColor: Int? = accentColor
        set(value) {
            if (field == value) return
            field = value
            themeChanged()
        }

    fun setColors(colors: ThemableIItemColors) {
        this.textColor = colors.textColor
        this.backgroundColor = colors.backgroundColor
        this.accentColor = colors.accentColor
    }

    fun themeChanged() {
        if (adapterItemCount == 0) return
        injectTheme(adapterItems)
        notifyAdapterDataSetChanged()
    }

    override fun add(position: Int, items: MutableList<Item>): FastItemAdapter<Item> {
        injectTheme(items)
        return super.add(position, items)
    }

    override fun add(position: Int, item: Item): FastItemAdapter<Item> {
        injectTheme(item)
        return super.add(position, item)
    }

    override fun add(item: Item): FastItemAdapter<Item> {
        injectTheme(item)
        return super.add(item)
    }

    override fun add(items: MutableList<Item>): FastItemAdapter<Item> {
        injectTheme(items)
        injectTheme(items)
        return super.add(items)
    }

    override fun set(items: MutableList<Item>?): FastItemAdapter<Item> {
        injectTheme(items)
        return super.set(items)
    }

    override fun set(position: Int, item: Item): FastItemAdapter<Item> {
        injectTheme(item)
        return super.set(position, item)
    }

    override fun setNewList(items: MutableList<Item>?, retainFilter: Boolean): FastItemAdapter<Item> {
        injectTheme(items)
        return super.setNewList(items, retainFilter)
    }

    override fun setNewList(items: MutableList<Item>?): FastItemAdapter<Item> {
        injectTheme(items)
        return super.setNewList(items)
    }

    override fun <T, S> setSubItems(collapsible: T, subItems: MutableList<S>?): T where S : IItem<*, *>?, T : IItem<*, *>?, T : IExpandable<T, S>?, S : ISubItem<Item, T>? {
        injectTheme(subItems)
        return super.setSubItems(collapsible, subItems)
    }

    internal fun injectTheme(items: Collection<IItem<*, *>?>?) {
        items?.forEach { injectTheme(it) }
    }

    internal fun injectTheme(item: IItem<*, *>?) {
        if (item is ThemableIItem && item.themeEnabled) {
            item.textColor = textColor
            item.backgroundColor = backgroundColor
            item.accentColor = accentColor
        }
    }
}

interface ThemableIItemColors {
    var textColor: Int?
    var backgroundColor: Int?
    var accentColor: Int?
}

class ThemableIItemColorsDelegate : ThemableIItemColors {
    override var textColor: Int? = null
    override var backgroundColor: Int? = null
    override var accentColor: Int? = null
}

/**
 * Interface that needs to be implemented by every iitem
 * Holds the color values and has helper methods to inject the colors
 */
interface ThemableIItem : ThemableIItemColors {
    var themeEnabled: Boolean
    fun bindTextColor(vararg views: TextView?)
    fun bindTextColorSecondary(vararg views: TextView?)
    fun bindDividerColor(vararg views: View?)
    fun bindAccentColor(vararg views: TextView?)
    fun bindBackgroundColor(vararg views: View?)
    fun bindBackgroundRipple(vararg views: View?)
    fun bindIconColor(vararg views: ImageView?)
}

/**
 * The delegate for [ThemableIItem]
 */
class ThemableIItemDelegate : ThemableIItem, ThemableIItemColors by ThemableIItemColorsDelegate() {
    override var themeEnabled: Boolean = true

    override fun bindTextColor(vararg views: TextView?) {
        val color = textColor ?: return
        views.forEach { it?.setTextColor(color) }
    }

    override fun bindTextColorSecondary(vararg views: TextView?) {
        val color = textColor?.adjustAlpha(0.8f) ?: return
        views.forEach { it?.setTextColor(color) }
    }

    override fun bindAccentColor(vararg views: TextView?) {
        val color = accentColor ?: textColor ?: return
        views.forEach { it?.setTextColor(color) }
    }

    override fun bindDividerColor(vararg views: View?) {
        val color = (textColor ?: accentColor)?.adjustAlpha(0.1f) ?: return
        views.forEach { it?.setBackgroundColor(color) }
    }

    override fun bindBackgroundColor(vararg views: View?) {
        val color = backgroundColor ?: return
        views.forEach { it?.setBackgroundColor(color) }
    }

    override fun bindBackgroundRipple(vararg views: View?) {
        val foreground = accentColor ?: textColor ?: return
        val background = backgroundColor ?: return
        val ripple = createSimpleRippleDrawable(foreground, background)
        views.forEach { it?.background = ripple }
    }

    override fun bindIconColor(vararg views: ImageView?) {
        val color = accentColor ?: textColor ?: return
        views.forEach { it?.drawable?.setTintList(ColorStateList.valueOf(color)) }
    }
}