package ca.allanwang.kau.sample

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import ca.allanwang.kau.email.sendEmail
import ca.allanwang.kau.kpref.KPrefActivity
import ca.allanwang.kau.kpref.KPrefAdapterBuilder
import ca.allanwang.kau.utils.*
import ca.allanwang.kau.views.RippleCanvas
import com.mikepenz.google_material_typeface_library.GoogleMaterial


class MainActivity : KPrefActivity() {

    override fun onCreateKPrefs(savedInstanceState: android.os.Bundle?): KPrefAdapterBuilder.() -> Unit = {
        textColor = { KPrefSample.textColor }
        accentColor = { KPrefSample.accentColor }

        header(R.string.header)

        /**
         * This is how the setup looks like with all the proper tags
         */
        checkbox(title = R.string.checkbox_1, getter = { KPrefSample.check1 }, setter = { KPrefSample.check1 = it },
                builder = {
                    descRes = R.string.desc
                })

        /**
         * Since we know the order, we may omit the tags
         */
        checkbox(R.string.checkbox_2, { KPrefSample.check2 }, { KPrefSample.check2 = it; reloadByTitle(R.string.checkbox_3) })

        /**
         * Since the builder is the last argument and is a lambda, we may write the setup cleanly like so:
         */
        checkbox(R.string.checkbox_3, { KPrefSample.check3 }, { KPrefSample.check3 = it }) {
            descRes = R.string.desc_dependent
            enabler = { KPrefSample.check2 }
            onDisabledClick = {
                itemView, _, _ ->
                itemView.context.toast("I am still disabled")
                true
            }
        }

        colorPicker(R.string.text_color, { KPrefSample.textColor }, { KPrefSample.textColor = it; reload() }) {
            descRes = R.string.color_custom
            allowCustom = true
        }

        colorPicker(R.string.accent_color, { KPrefSample.accentColor }, {
            KPrefSample.accentColor = it
            reload()
            val darkerColor = it.darken()
            this@MainActivity.navigationBarColor = darkerColor
            toolbarCanvas.ripple(darkerColor, RippleCanvas.MIDDLE, RippleCanvas.END, duration = 500L)
        }) {
            descRes = R.string.color_no_custom
            allowCustom = false
        }

        colorPicker(R.string.background_color, { KPrefSample.bgColor }, {
            KPrefSample.bgColor = it; bgCanvas.ripple(it, duration = 500L)
        }) {
            iicon = GoogleMaterial.Icon.gmd_colorize
            descRes = R.string.color_custom_alpha
            allowCustomAlpha = true
            allowCustom = true
        }

        text<String>(R.string.text, { KPrefSample.text }, { KPrefSample.text = it }) {
            descRes = R.string.text_desc
            onClick = {
                itemView, _, item ->
                itemView.context.materialDialog {
                    title("Type Text")
                    input("Type here", item.pref, {
                        _, input ->
                        item.pref = input.toString()
                        reloadByTitle(R.string.text)
                    })
                    inputRange(0, 20)
                }
                true
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bgCanvas.set(KPrefSample.bgColor)
        val darkAccent = KPrefSample.accentColor.darken()
        toolbarCanvas.set(darkAccent)
        this.navigationBarColor = darkAccent
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> startActivity(AnimActivity::class.java, clearStack = true)
            R.id.action_email -> sendEmail(R.string.your_email, R.string.your_subject)
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

}
