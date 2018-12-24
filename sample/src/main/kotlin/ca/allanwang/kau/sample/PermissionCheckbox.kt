package ca.allanwang.kau.sample

import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ca.allanwang.kau.iitems.KauIItem
import ca.allanwang.kau.utils.hasPermission

/**
 * Created by Allan Wang on 2017-07-03.
 */
class PermissionCheckbox(val permission: String) : KauIItem<PermissionCheckbox, PermissionCheckbox.ViewHolder>(
    R.layout.permission_checkbox, { ViewHolder(it) }) {

    override fun bindView(holder: ViewHolder, payloads: MutableList<Any>) {
        super.bindView(holder, payloads)
        holder.text.text = permission
        holder.checkbox.isChecked = holder.itemView.context.hasPermission(permission)
        holder.checkbox.isClickable = false
        holder.checkbox.jumpDrawablesToCurrentState() //Cancel the animation
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val text: TextView = v.findViewById(R.id.perm_text)
        val checkbox: CheckBox = v.findViewById(R.id.perm_checkbox)
    }
}