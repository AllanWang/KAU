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
package ca.allanwang.kau.kpref.activity

import androidx.annotation.StringRes
import ca.allanwang.kau.kpref.activity.items.KPrefCheckbox
import ca.allanwang.kau.kpref.activity.items.KPrefColorPicker
import ca.allanwang.kau.kpref.activity.items.KPrefHeader
import ca.allanwang.kau.kpref.activity.items.KPrefItemBase
import ca.allanwang.kau.kpref.activity.items.KPrefItemCore
import ca.allanwang.kau.kpref.activity.items.KPrefPlainText
import ca.allanwang.kau.kpref.activity.items.KPrefSeekbar
import ca.allanwang.kau.kpref.activity.items.KPrefSubItems
import ca.allanwang.kau.kpref.activity.items.KPrefText
import ca.allanwang.kau.kpref.activity.items.KPrefTimePicker

/**
 * Created by Allan Wang on 2017-06-08.
 *
 * Houses all the components that can be called externally to setup the kpref mainAdapter
 */
@DslMarker
annotation class KPrefMarker

/**
 * Contains attributes shared amongst all kpref items
 */
@KPrefMarker
interface CoreAttributeContract {
    var textColor: (() -> Int)?
    var accentColor: (() -> Int)?
}

/**
 * Implementation of [CoreAttributeContract]
 */
class CoreAttributeBuilder : CoreAttributeContract {
    override var textColor: (() -> Int)? = null
    override var accentColor: (() -> Int)? = textColor
}

interface KPrefActivityContract {
    fun showNextPrefs(@StringRes toolbarTitleRes: Int, builder: KPrefAdapterBuilder.() -> Unit)
    val hasPrevPrefs: Boolean
    fun showPrevPrefs()
    fun reloadByTitle(@StringRes vararg title: Int)
}

class GlobalOptions(
    core: CoreAttributeContract,
    activity: KPrefActivityContract
) : CoreAttributeContract by core, KPrefActivityContract by activity

/**
 * Builder for kpref items
 * Contains DSLs for every possible item
 * The arguments are all the mandatory values plus an optional builder housing all the possible configurations
 * The mandatory values are final so they cannot be edited in the builder
 *
 * This function will be called asynchronously, so don't worry about blocking the thread
 * The recycler will only animate once this is completed though
 */
@KPrefMarker
class KPrefAdapterBuilder(val globalOptions: GlobalOptions) {

    @KPrefMarker
    fun header(@StringRes title: Int) = list.add(KPrefHeader(KPrefItemCore.CoreBuilder(globalOptions, title)))

    @KPrefMarker
    fun checkbox(
        @StringRes title: Int,
        getter: () -> Boolean,
        setter: KPrefItemActions.(value: Boolean) -> Unit,
        builder: KPrefItemBase.BaseContract<Boolean>.() -> Unit = {}
    ) = list.add(
        KPrefCheckbox(KPrefItemBase.BaseBuilder(globalOptions, title, getter, setter)
            .apply { builder() })
    )

    @KPrefMarker
    fun colorPicker(
        @StringRes title: Int,
        getter: () -> Int,
        setter: KPrefItemActions.(value: Int) -> Unit,
        builder: KPrefColorPicker.KPrefColorContract.() -> Unit = {}
    ) = list.add(
        KPrefColorPicker(KPrefColorPicker.KPrefColorBuilder(globalOptions, title, getter, setter)
            .apply { builder() })
    )

    @KPrefMarker
    fun <T> text(
        @StringRes title: Int,
        getter: () -> T,
        setter: KPrefItemActions.(value: T) -> Unit,
        builder: KPrefText.KPrefTextContract<T>.() -> Unit = {}
    ) = list.add(
        KPrefText(KPrefText.KPrefTextBuilder(globalOptions, title, getter, setter)
            .apply { builder() })
    )

    @KPrefMarker
    fun subItems(
        @StringRes title: Int,
        itemBuilder: KPrefAdapterBuilder.() -> Unit,
        builder: KPrefSubItems.KPrefSubItemsContract.() -> Unit
    ) = list.add(
        KPrefSubItems(KPrefSubItems.KPrefSubItemsBuilder(globalOptions, title, itemBuilder)
            .apply { builder() })
    )

    @KPrefMarker
    fun plainText(
        @StringRes title: Int,
        builder: KPrefItemBase.BaseContract<Unit>.() -> Unit = {}
    ) = list.add(
        KPrefPlainText(KPrefPlainText.KPrefPlainTextBuilder(globalOptions, title)
            .apply { builder() })
    )

    @KPrefMarker
    fun seekbar(
        @StringRes title: Int,
        getter: () -> Int,
        setter: KPrefItemActions.(value: Int) -> Unit,
        builder: KPrefSeekbar.KPrefSeekbarContract.() -> Unit = {}
    ) = list.add(
        KPrefSeekbar(KPrefSeekbar.KPrefSeekbarBuilder(globalOptions, title, getter, setter)
            .apply { builder() })
    )

    @KPrefMarker
    fun timePicker(
        @StringRes title: Int,
        getter: () -> Int,
        setter: KPrefItemActions.(value: Int) -> Unit,
        builder: KPrefTimePicker.KPrefTimeContract.() -> Unit = {}
    ) = list.add(
        KPrefTimePicker(KPrefTimePicker.KPrefTimeBuilder(globalOptions, title, getter, setter)
            .apply { builder() })
    )

    @KPrefMarker
    val list: MutableList<KPrefItemCore> = mutableListOf()
}
