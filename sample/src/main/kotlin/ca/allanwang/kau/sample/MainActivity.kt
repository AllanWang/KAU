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

        checkbox(title = R.string.checkbox_1, coreBuilder = {
            description = R.string.desc
        }, itemBuilder = {
            getter = { KPrefSample.check1 }
            setter = { KPrefSample.check1 = it }
        })

        checkbox(title = R.string.checkbox_2, itemBuilder = {
            getter = { KPrefSample.check2 }
            setter = { KPrefSample.check2 = it; reloadByTitle(R.string.checkbox_3) }
        })

        checkbox(title = R.string.checkbox_3, coreBuilder = {
            description = R.string.desc_dependent
        }, itemBuilder = {
            enabler = { KPrefSample.check2 }
            getter = { KPrefSample.check3 }
            setter = { KPrefSample.check3 = it }
            onDisabledClick = {
                itemView, _, _ ->
                itemView.context.toast("I am still disabled")
                true
            }
        })

        colorPicker(title = R.string.text_color, coreBuilder = {
            description = R.string.color_custom
        }, itemBuilder = {
            getter = { KPrefSample.textColor }
            setter = { KPrefSample.textColor = it; reload() }
        }, colorBuilder = {
            allowCustom = true
        })

        colorPicker(title = R.string.accent_color, coreBuilder = {
            description = R.string.color_no_custom
        }, itemBuilder = {
            getter = { KPrefSample.accentColor }
            setter = {
                KPrefSample.accentColor = it
                reload()
                val darkerColor = it.darken()
                this@MainActivity.navigationBarColor = darkerColor
                toolbarCanvas.ripple(darkerColor, RippleCanvas.MIDDLE, RippleCanvas.END, duration = 500L)
            }
        }, colorBuilder = {
            allowCustom = false
        })

        colorPicker(title = R.string.background_color, coreBuilder = {
            iicon = GoogleMaterial.Icon.gmd_colorize
            description = R.string.color_custom_alpha
        }, itemBuilder = {
            getter = { KPrefSample.bgColor }
            setter = { KPrefSample.bgColor = it; bgCanvas.ripple(it, duration = 500L) }
        }, colorBuilder = {
            allowCustomAlpha = true
            allowCustom = true
        })

        text<String>(title = R.string.text, coreBuilder = {
            description = R.string.text_desc
        }, itemBuilder = {
            getter = { KPrefSample.text }
            setter = { KPrefSample.text = it }
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
                    build()
                }.show()
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
