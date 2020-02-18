package ca.allanwang.kau.kpref

import android.content.Context

interface KPrefFactory {
    fun createBuilder(preferenceName: String): KPrefBuilder
}

/**
 * Default factory for Android preferences
 */
class KPrefFactoryAndroid(context: Context) : KPrefFactory {

    val context: Context = context.applicationContext

    override fun createBuilder(preferenceName: String): KPrefBuilder =
        KPrefBuilderAndroid(context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE))
}

object KPrefFactoryInMemory : KPrefFactory {
    override fun createBuilder(preferenceName: String): KPrefBuilder = KPrefBuilderInMemory
}