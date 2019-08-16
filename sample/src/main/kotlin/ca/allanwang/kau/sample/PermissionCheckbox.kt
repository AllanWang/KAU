/*
 * Copyright 2018 Allan Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
class PermissionCheckbox(val permission: String) : KauIItem<PermissionCheckbox.ViewHolder>(
    R.layout.permission_checkbox, { ViewHolder(it) }) {

    override fun bindView(holder: ViewHolder, payloads: MutableList<Any>) {
        super.bindView(holder, payloads)
        holder.text.text = permission
        holder.checkbox.isChecked = holder.itemView.context.hasPermission(permission)
        holder.checkbox.isClickable = false
        holder.checkbox.jumpDrawablesToCurrentState() // Cancel the animation
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val text: TextView = v.findViewById(R.id.perm_text)
        val checkbox: CheckBox = v.findViewById(R.id.perm_checkbox)
    }
}
