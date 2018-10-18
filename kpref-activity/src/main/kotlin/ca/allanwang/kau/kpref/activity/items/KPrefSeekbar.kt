package ca.allanwang.kau.kpref.activity.items

import android.widget.SeekBar
import android.widget.TextView
import ca.allanwang.kau.kpref.activity.GlobalOptions
import ca.allanwang.kau.kpref.activity.KClick
import ca.allanwang.kau.kpref.activity.R
import ca.allanwang.kau.utils.tint

/**
 * Created by Allan Wang on 2017-06-07.
 *
 * Checkbox preference
 * When clicked, will toggle the preference and the apply the result to the checkbox
 */
open class KPrefSeekbar(val builder: KPrefSeekbarContract) : KPrefItemBase<Int>(builder) {

    protected val min get() = builder.min
    protected val max get() = builder.max
    protected val increment get() = builder.increments

    override fun KClick<Int>.defaultOnClick() = Unit

    override fun bindView(holder: ViewHolder, payloads: List<Any>) {
        super.bindView(holder, payloads)
        val text = holder.bindInnerView<TextView>(R.layout.kau_pref_seekbar_text)
        withTextColor(text::setTextColor)

        val tvc = builder.textViewConfigs

        text.tvc()
        val seekbar = holder.bindLowerView<SeekBar>(R.layout.kau_pref_seekbar) {
            it.max = (max - min) / increment
            it.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(sb: SeekBar, progress: Int, fromUser: Boolean) {
                    text.text = builder.toText(progress.fromProgress)
                }

                override fun onStartTrackingTouch(sb: SeekBar) {}

                override fun onStopTrackingTouch(sb: SeekBar) {
                    pref = sb.progress.fromProgress
                }
            })
        }
        withAccentColor(seekbar::tint)
        text.text = builder.toText(seekbar.progress.fromProgress) //set initial text in case no change occurs
        seekbar.progress = pref.toProgress
        seekbar.isEnabled = builder.enabler()
    }

    /**
     * Extension of the base contract
     */
    interface KPrefSeekbarContract : BaseContract<Int> {
        var min: Int
        var max: Int
        var increments: Int
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
            titleId: Int,
            getter: () -> Int,
            setter: (value: Int) -> Unit
    ) : KPrefSeekbarContract, BaseContract<Int> by BaseBuilder(globalOptions, titleId, getter, setter) {

        override var min: Int = 0

        override var max: Int = 100

        override var increments: Int = 1
            set(value) {
                if (value <= 0)
                    throw IllegalArgumentException("Seekbar must increment by at least 1")
                field = value
            }

        override var toText: (Int) -> String = Int::toString

        override var textViewConfigs: TextView.() -> Unit = {}
    }

    /**
     * Helper to convert true value to progress value
     * Progress values start at 0 and increment by 1
     */
    protected inline val Int.toProgress: Int
        get() = (this - min) / increment

    /**
     * Inverse of [Int.toProgress] to find the true value from the seekbar
     */
    protected inline val Int.fromProgress: Int
        get() = this * increment + min

    override fun getType(): Int = R.id.kau_item_pref_seekbar

}