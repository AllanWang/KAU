package ca.allanwang.kau.kpref.items

import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import ca.allanwang.kau.R
import ca.allanwang.kau.kpref.GlobalOptions
import ca.allanwang.kau.kpref.KPrefException
import ca.allanwang.kau.logging.KL
import ca.allanwang.kau.utils.tint

/**
 * Created by Allan Wang on 2017-06-07.
 *
 * Checkbox preference
 * When clicked, will toggle the preference and the apply the result to the checkbox
 */
open class KPrefSeekbar(val builder: KPrefSeekbarContract) : KPrefItemBase<Int>(builder) {


    override fun defaultOnClick(itemView: View, innerContent: View?): Boolean = false

    override fun onPostBindView(viewHolder: ViewHolder, textColor: Int?, accentColor: Int?) {
        super.onPostBindView(viewHolder, textColor, accentColor)
        val text = viewHolder.bindInnerView<TextView>(R.layout.kau_preference_seekbar_text)
        if (textColor != null) text.setTextColor(textColor)
        val tvc = builder.textViewConfigs

        text.tvc()
        val seekbar = viewHolder.bindLowerView<SeekBar>(R.layout.kau_preference_seekbar) {
            it.max = builder.range
            it.incrementProgressBy(builder.increments)
            it.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(sb: SeekBar, progress: Int, fromUser: Boolean) {
                    text.text = builder.toText(progress.fromProgress)
                }

                override fun onStartTrackingTouch(sb: SeekBar) {

                }

                override fun onStopTrackingTouch(sb: SeekBar) {
                    val trueProgress = sb.progress.fromProgress
                    pref = trueProgress
                    KL.d("New pref $trueProgress")
                }
            })
        }
        if (accentColor != null) seekbar.tint(accentColor)
        seekbar.progress = pref.toProgress
    }

    /**
     * Extension of the base contract
     */
    interface KPrefSeekbarContract : BaseContract<Int> {
        var min: Int
        var max: Int
        var increments: Int
        var range: Int
        /**
         * Once a seekbar is let go, calculates what text to show in the text view
         */
        var toText: (Int) -> String
        var textViewConfigs: TextView.() -> Unit
    }

    /**
     * Default implementation of [KPrefSeekbarContract]
     */
    class KPrefSeekbarBuilder(
            globalOptions: GlobalOptions,
            titleRes: Int,
            getter: () -> Int,
            setter: (value: Int) -> Unit
    ) : KPrefSeekbarContract, BaseContract<Int> by BaseBuilder(globalOptions, titleRes, getter, setter) {

        override var min: Int = 0
            set(value) {
                field = value
                range = -1
            }
        override var max: Int = 100
            set(value) {
                field = value
                range = -1
            }
        override var increments: Int = 1

        override var range: Int = max - min
                //value doesn't matter; setting will prompt the check
            set(value) {
                if (max <= min) throw KPrefException("Range min ($min) must be smaller than max ($max)")
                field = max - min
            }

        override var toText: (Int) -> String = { it.toString() }

        override var textViewConfigs: TextView.() -> Unit = {}
    }

    val Int.toProgress: Int
        get() = this - builder.min

    val Int.fromProgress: Int
        get() = this + builder.min

    override fun getType(): Int = R.id.kau_item_pref_seekbar

}