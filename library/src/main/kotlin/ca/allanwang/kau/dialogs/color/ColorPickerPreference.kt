package ca.allanwang.kau.dialogs.color

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.support.annotation.ColorInt
import android.support.annotation.StringRes
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.Preference
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.*
import ca.allanwang.kau.R
import ca.allanwang.kau.utils.ANDROID_NAMESPACE
import ca.allanwang.kau.utils.integer
import ca.allanwang.kau.utils.resolveColor
import ca.allanwang.kau.utils.toColor
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.color.CircleView
import com.afollestad.materialdialogs.color.ColorChooserDialog
import com.afollestad.materialdialogs.internal.MDTintHelper
import com.afollestad.materialdialogs.util.DialogUtils
import java.util.*

/**
 * Created by Allan Wang on 2017-06-08.
 */
class ColorPickerPreference @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0
) : Preference(context, attrs, defStyleAttr, defStyleRes), Preference.OnPreferenceClickListener {

    var defaultColor: Int = Color.BLACK
    var currentColor: Int
    var accentMode = false
    var dialogTitle: Int = 0

    init {
        onPreferenceClickListener = this
        if (attrs != null) {
            val defaultValue = attrs.getAttributeValue(ANDROID_NAMESPACE, "defaultValue")
            if (defaultValue?.startsWith("#") ?: false) {
                try {
                    defaultColor = defaultValue.toColor()
                } catch (e: IllegalArgumentException) {
                    throw IllegalArgumentException("ColorPickerPreference $key has a default value that is not a color resource: $defaultValue")
                }
            } else {
                val resourceId = attrs.getAttributeResourceValue(ANDROID_NAMESPACE, "defaultValue", 0)
                if (resourceId != 0)
                    defaultColor = context.integer(resourceId)
                else
                    throw IllegalArgumentException("ColorPickerPreference $key has a default value that is not a color resource: $defaultValue")
            }
        }
        currentColor = getPersistedInt(defaultColor)
    }

    override fun onPreferenceClick(preference: Preference): Boolean {
        context.colorPickerDialog {

        }.show()
        return true
    }
}