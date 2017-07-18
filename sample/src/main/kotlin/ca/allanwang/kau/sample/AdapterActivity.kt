package ca.allanwang.kau.sample

import android.os.Bundle
import ca.allanwang.kau.iitems.CardIItem
import ca.allanwang.kau.ui.activities.ElasticRecyclerActivity
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import com.mikepenz.google_material_typeface_library.GoogleMaterial

/**
 * Created by Allan Wang on 2017-07-17.
 */
class AdapterActivity : ElasticRecyclerActivity() {

    val adapter = FastItemAdapter<IItem<*, *>>()

    override fun onCreate(savedInstanceState: Bundle?, configs: Configs):Boolean {
        recycler.adapter = adapter
        adapter.add(CardIItem {
            titleRes = R.string.kau_text_copied
            descRes = R.string.kau_lorem_ipsum
            imageIIcon = GoogleMaterial.Icon.gmd_file_download
        })
        return true
    }
}