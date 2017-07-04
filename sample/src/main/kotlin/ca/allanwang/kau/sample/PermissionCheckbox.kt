package ca.allanwang.kau.sample

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import ca.allanwang.kau.iitems.KotlinIItem
import ca.allanwang.kau.utils.bindView

/**
 * Created by Allan Wang on 2017-07-03.
 */
class PermissionCheckbox(val permission: String) : KotlinIItem<PermissionCheckbox, PermissionCheckbox.ViewHolder>(
        R.layout.permission_checkbox, R.layout.permission_checkbox, { ViewHolder(it) }) {

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val text: TextView by bindView(R.id.perm_text)
        val checkbox: CheckBox by bindView(R.id.perm_checkbox)
    }
}