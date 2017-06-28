package ca.allanwang.kau.about

import android.graphics.drawable.Drawable
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import ca.allanwang.kau.R
import ca.allanwang.kau.utils.bindView
import com.mikepenz.fastadapter.items.AbstractItem

/**
 * Created by Allan Wang on 2017-06-27.
 */
class MainItem(builder: Config.() -> Unit) : AbstractItem<MainItem, MainItem.ViewHolder>() {

    val configs = Config().apply { builder() }

    class Config {
        var icon: Drawable? = null
        var title: String = "App Title"
        var author: String? = null
        var description: String? = null
        var version: String? = null
        var githubLink: String? = null
    }

    override fun getType(): Int = R.id.kau_item_about_main

    override fun getLayoutRes(): Int = R.layout.kau_about_item_main

    override fun isSelectable(): Boolean = false

    override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

    override fun bindView(holder: ViewHolder, payloads: MutableList<Any>?) {
        super.bindView(holder, payloads)
        with (holder) {
            title.text = configs.title
            creator.text = configs.author
            description.text = configs.description
//            license.text = configs.description
        }
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val card: CardView by bindView(R.id.container)
        val title: TextView by bindView(R.id.title)
        val creator: TextView by bindView(R.id.creator)
        val description: TextView by bindView(R.id.description)
        val version: TextView by bindView(R.id.version)
        val license: TextView by bindView(R.id.license)
        val bottomContainer: LinearLayout by bindView(R.id.bottom_container)

        val divider: View by bindView(R.id.top_divider)
        val bottomDivider: View by bindView(R.id.bottom_divider)
    }
}
