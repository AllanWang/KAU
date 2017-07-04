package ca.allanwang.kau.imagepicker

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.allanwang.kau.R
import ca.allanwang.kau.adapters.FastItemThemedAdapter
import ca.allanwang.kau.adapters.ThemableIItemColors
import ca.allanwang.kau.adapters.ThemableIItemColorsDelegate
import ca.allanwang.kau.iitems.LibraryIItem
import ca.allanwang.kau.utils.bindView
import ca.allanwang.kau.utils.string
import ca.allanwang.kau.widgets.ElasticDragDismissFrameLayout
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import java.security.InvalidParameterException

/**
 * Created by Allan Wang on 2017-07-04.
 *
 */
abstract class ImagePickerActivityBase : AppCompatActivity() {

    val toolbar:Toolbar by bindView(R.id.kau_toolbar)
    val recycler:RecyclerView by bindView(R.id.kau_recycler)
    val adapter = FastItemAdapter<ImageItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.kau_activity_image_picker)
    }
}