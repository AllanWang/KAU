package ca.allanwang.kau.sample

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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

        checkbox(title = R.string.checkbox_1,
                getter = { KPrefSample.check1 },
                setter = { KPrefSample.check1 = it },
                builder = {
                    descRes = R.string.desc
                })

        checkbox(title = R.string.checkbox_2,
                getter = { KPrefSample.check2 },
                setter = { KPrefSample.check2 = it; reloadByTitle(R.string.checkbox_3) })

        checkbox(title = R.string.checkbox_3,
                getter = { KPrefSample.check3 },
                setter = { KPrefSample.check3 = it },
                builder = {
                    descRes = R.string.desc_dependent
                    enabler = { KPrefSample.check2 }
                    onDisabledClick = {
                        itemView, _, _ ->
                        itemView.context.toast("I am still disabled")
                        true
                    }
                })

        colorPicker(title = R.string.text_color,
                getter = { KPrefSample.textColor },
                setter = { KPrefSample.textColor = it; reload() },
                builder = {
                    descRes = R.string.color_custom
                    allowCustom = true
                })

        colorPicker(title = R.string.accent_color,
                getter = { KPrefSample.accentColor },
                setter = {
                    KPrefSample.accentColor = it
                    reload()
                    val darkerColor = it.darken()
                    this@MainActivity.navigationBarColor = darkerColor
                    toolbarCanvas.ripple(darkerColor, RippleCanvas.MIDDLE, RippleCanvas.END, duration = 500L)
                },
                builder = {
                    descRes = R.string.color_no_custom
                    allowCustom = false
                })

        colorPicker(title = R.string.background_color,
                getter = { KPrefSample.bgColor },
                setter = { KPrefSample.bgColor = it; bgCanvas.ripple(it, duration = 500L) },
                builder = {
                    iicon = GoogleMaterial.Icon.gmd_colorize
                    descRes = R.string.color_custom_alpha
                    allowCustomAlpha = true
                    allowCustom = true
                })

        text<String>(title = R.string.text,
                getter = { KPrefSample.text },
                setter = { KPrefSample.text = it },
                builder = {
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
                })
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
            R.id.action_settings -> {
                startActivitySlideIn(AnimActivity::class.java, clearStack = true)
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

}
