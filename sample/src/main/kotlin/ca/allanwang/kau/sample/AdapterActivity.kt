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
package ca.allanwang.kau.sample

import android.os.Bundle
import ca.allanwang.kau.adapters.fastAdapter
import ca.allanwang.kau.iitems.CardIItem
import ca.allanwang.kau.ui.activities.ElasticRecyclerActivity
import ca.allanwang.kau.utils.toast
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.google_material_typeface_library.GoogleMaterial

/**
 * Created by Allan Wang on 2017-07-17.
 */
class AdapterActivity : ElasticRecyclerActivity() {

    override fun onCreate(savedInstanceState: Bundle?, configs: Configs): Boolean {
        val adapter = ItemAdapter<IItem<*>>()
        recycler.adapter = fastAdapter(adapter)
        adapter.add(
            listOf(
                CardIItem {
                    titleRes = R.string.kau_text_copied
                    descRes = R.string.kau_lorem_ipsum
                    imageIIcon = GoogleMaterial.Icon.gmd_file_download
                },
                CardIItem {
                    titleRes = R.string.kau_text_copied
                    descRes = R.string.kau_lorem_ipsum
                },
                CardIItem {
                    titleRes = R.string.kau_text_copied
                    imageIIcon = GoogleMaterial.Icon.gmd_file_download
                    cardClick = { toast("Card click") }
                },
                CardIItem {
                    titleRes = R.string.kau_text_copied
                    descRes = R.string.kau_lorem_ipsum
                    imageIIcon = GoogleMaterial.Icon.gmd_file_download
                    button = "Test"
                    buttonClick = { toast("T") }
                },
                CardIItem {
                    titleRes = R.string.kau_text_copied
                    button = "Test"
                    buttonClick = { toast("HI") }
                })
        )
        setOutsideTapListener { finishAfterTransition() }
        return true
    }
}
