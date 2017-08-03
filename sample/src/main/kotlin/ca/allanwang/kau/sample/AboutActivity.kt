package ca.allanwang.kau.sample

import ca.allanwang.kau.about.AboutActivityBase
import ca.allanwang.kau.adapters.FastItemThemedAdapter
import ca.allanwang.kau.iitems.CardIItem
import com.mikepenz.fastadapter.IItem

/**
 * Created by Allan Wang on 2017-06-27.
 */
class AboutActivity : AboutActivityBase(R.string::class.java, {
    cutoutDrawableRes = R.drawable.kau
    textColor = 0xde000000.toInt()
    backgroundColor = 0xfffafafa.toInt()
    accentColor = 0xff00838F.toInt()
    cutoutForeground = 0xff18FFFF.toInt()
    faqXmlRes = R.xml.kau_faq
    faqParseNewLine = false
}) {

    override fun postInflateMainPage(adapter: FastItemThemedAdapter<IItem<*, *>>) {
        adapter.add(CardIItem {
            title = "About KAU"
            descRes = R.string.about_kau
        })
    }
}