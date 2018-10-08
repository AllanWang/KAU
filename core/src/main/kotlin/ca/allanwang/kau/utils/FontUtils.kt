package ca.allanwang.kau.utils

import android.content.Context
import android.graphics.Typeface

/**
 * Created by Allan Wang on 2017-06-28.
 */
object FontUtils {

    private val sTypefaceCache: MutableMap<String, Typeface> = mutableMapOf()

    fun get(context: Context, font: String): Typeface {
        synchronized(sTypefaceCache) {
            if (!sTypefaceCache.containsKey(font)) {
                val tf = Typeface.createFromAsset(
                        context.applicationContext.assets, "fonts/$font.ttf")
                sTypefaceCache.put(font, tf)
            }
            return sTypefaceCache.get(font)
                    ?: throw IllegalArgumentException("Font error; typeface does not exist at assets/fonts$font.ttf")
        }
    }

    fun getName(typeface: Typeface): String? = sTypefaceCache.entries.firstOrNull { it.value == typeface }?.key

}

fun Context.getFont(font: String) = FontUtils.get(this, font)
fun Context.getFontName(typeface: Typeface) = FontUtils.getName(typeface)