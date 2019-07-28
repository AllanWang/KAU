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
package ca.allanwang.kau.kpref.activity.items

import android.view.View
import ca.allanwang.kau.kpref.activity.GlobalOptions
import ca.allanwang.kau.kpref.activity.KPrefAdapterBuilder
import ca.allanwang.kau.kpref.activity.R

/**
 * Created by Allan Wang on 2017-06-14.
 *
 * Sub item preference
 * When clicked, will navigate to a new set of preferences and add the old list to a stack
 *
 */
open class KPrefSubItems(open val builder: KPrefSubItemsContract) : KPrefItemCore(builder) {

    override fun onClick(itemView: View) {
        builder.globalOptions.showNextPrefs(builder.titleFun(), builder.itemBuilder)
    }

    override val layoutRes: Int
        get() = R.layout.kau_pref_core

    /**
     * Extension of the base contract with an optional text getter
     */
    interface KPrefSubItemsContract : CoreContract {
        val itemBuilder: KPrefAdapterBuilder.() -> Unit
    }

    /**
     * Default implementation of [KPrefTextContract]
     */
    class KPrefSubItemsBuilder(
        globalOptions: GlobalOptions,
        titleId: Int,
        override val itemBuilder: KPrefAdapterBuilder.() -> Unit
    ) : KPrefSubItemsContract, CoreContract by CoreBuilder(globalOptions, titleId)

    override val type: Int
        get() = R.id.kau_item_pref_sub_item
}
