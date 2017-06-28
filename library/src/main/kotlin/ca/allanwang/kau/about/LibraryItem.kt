package ca.allanwang.kau.about

/**
 * Created by Allan Wang on 2017-06-27.
 */

import android.os.Build
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import ca.allanwang.kau.R
import ca.allanwang.kau.utils.bindView
import ca.allanwang.kau.utils.gone
import ca.allanwang.kau.utils.visible
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.fastadapter.items.AbstractItem


/**
 * Created by mikepenz on 28.12.15.
 */
class LibraryItem(val lib: Library) : AbstractItem<LibraryItem, LibraryItem.ViewHolder>() {

    override fun getType(): Int = R.id.kau_item_about_library

    override fun getLayoutRes(): Int = R.layout.kau_about_item_library

    override fun isSelectable(): Boolean = false

    override fun bindView(holder: ViewHolder, payloads: MutableList<Any>?) {
        super.bindView(holder, payloads)
        with(holder) {
            name.text = lib.libraryName
            creator.text = lib.author
            description.text = if (lib.libraryDescription.isBlank()) lib.libraryDescription
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                Html.fromHtml(lib.libraryDescription, Html.FROM_HTML_MODE_LEGACY)
            else Html.fromHtml(lib.libraryDescription)
            bottomDivider.gone()
            bottomContainer.gone()
            if (lib.libraryVersion?.isNotBlank() ?: false) {
                bottomDivider.visible()
                bottomContainer.visible()
                version.text = lib.libraryVersion
            }
            if (lib.license?.licenseName?.isNotBlank() ?: false) {
                bottomDivider.visible()
                bottomContainer.visible()
                license.text = lib.license?.licenseName
            }
        }
    }


    override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val card: CardView by bindView(R.id.rippleForegroundListenerView)
        val name: TextView by bindView(R.id.libraryName)
        val creator: TextView by bindView(R.id.libraryCreator)
        val description: TextView by bindView(R.id.libraryDescription)
        val version: TextView by bindView(R.id.libraryVersion)
        val license: TextView by bindView(R.id.libraryLicense)
        val bottomContainer: LinearLayout by bindView(R.id.libraryBottomContainer)

        val divider: View by bindView(R.id.libraryDescriptionDivider)
        val bottomDivider: View by bindView(R.id.libraryBottomDivider)
    }

}