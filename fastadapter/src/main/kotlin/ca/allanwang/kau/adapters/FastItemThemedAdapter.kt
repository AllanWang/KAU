/*
 * Copyright 2018 Allan Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ca.allanwang.kau.adapters

import android.content.res.ColorStateList
import android.os.Build
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import ca.allanwang.kau.ui.createSimpleRippleDrawable
import ca.allanwang.kau.utils.adjustAlpha
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.adapters.FastItemAdapter

/**
 * Created by Allan Wang on 2017-06-29.
 *
 * Adapter with a set of colors that will be added to all subsequent items
 * Changing a color while the adapter is not empty will reload all items
 *
 * This adapter overrides every method where an item is added
 * If that item extends [ThemableIItem], then the colors will be set
 */
class FastItemThemedAdapter<Item : GenericItem>(
    textColor: Int? = null,
    backgroundColor: Int? = null,
    accentColor: Int? = null
) : FastItemAdapter<Item>() {
    constructor(colors: ThemableIItemColors) : this(
        colors.textColor,
        colors.backgroundColor,
        colors.accentColor
    )

    init {
        itemAdapter.interceptor = {
            injectTheme(it)
            it
        }
    }

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
        if (adapterItemCount == 0) {
            return
        }
        injectTheme(adapterItems)
        notifyAdapterDataSetChanged()
    }

    private fun injectTheme(items: Collection<GenericItem?>?) {
        items?.forEach { injectTheme(it) }
    }

    protected fun injectTheme(item: GenericItem?) {
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

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun bindBackgroundRipple(vararg views: View?) {
        val background = backgroundColor ?: return
        val foreground = accentColor ?: textColor ?: backgroundColor
        ?: return // default to normal background
        val ripple = createSimpleRippleDrawable(foreground, background)
        views.forEach { it?.background = ripple }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun bindIconColor(vararg views: ImageView?) {
        val color = accentColor ?: textColor ?: return
        views.forEach { it?.drawable?.setTintList(ColorStateList.valueOf(color)) }
    }
}
