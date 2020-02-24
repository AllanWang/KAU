/*
 * Copyright 2020 Allan Wang
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
