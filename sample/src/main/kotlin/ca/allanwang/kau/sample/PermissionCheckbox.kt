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

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ca.allanwang.fastadapter.viewbinding.BindingClickEventHook
import ca.allanwang.fastadapter.viewbinding.BindingItem
import ca.allanwang.fastadapter.viewbinding.BindingLayout
import ca.allanwang.fastadapter.viewbinding.VhModel
import ca.allanwang.kau.iitems.KauIItem
import ca.allanwang.kau.logging.KL
import ca.allanwang.kau.permissions.kauRequestPermissions
import ca.allanwang.kau.sample.databinding.PermissionCheckboxBinding
import ca.allanwang.kau.utils.hasPermission
import ca.allanwang.kau.utils.toast
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.listeners.EventHook

/**
 * Created by Allan Wang on 2017-07-03.
 */
class PermissionCheckbox(val permission: String) : KauIItem<PermissionCheckbox.ViewHolder>(
    R.layout.permission_checkbox, { ViewHolder(it) }) {

    override fun bindView(holder: ViewHolder, payloads: List<Any>) {
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

data class PermissionCheckboxModel(val permission: String) : VhModel {
    override fun vh(): GenericItem = PermissionCheckboxViewBinding(this)
}

class PermissionCheckboxViewBinding(
    override val data: PermissionCheckboxModel
) : BindingItem<PermissionCheckboxBinding>(data),
    BindingLayout<PermissionCheckboxBinding> by Companion {

    override fun createBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup?
    ): PermissionCheckboxBinding =
        PermissionCheckboxBinding.inflate(layoutInflater, parent, false)

    override fun PermissionCheckboxBinding.bindView(
        holder: ViewHolder,
        payloads: List<Any>
    ) {
        permText.text = data.permission
        permCheckbox.apply {
            isChecked = holder.itemView.context.hasPermission(data.permission)
            isFocusable = false
            isClickable = false
            jumpDrawablesToCurrentState() // Cancel the animation
        }
    }

    companion object : BindingLayout<PermissionCheckboxBinding> {
        override val layoutRes: Int
            get() = R.layout.permission_checkbox

        fun clickHook(): EventHook<PermissionCheckboxViewBinding> = object : BindingClickEventHook<PermissionCheckboxBinding, PermissionCheckboxViewBinding>(this) {
            override fun PermissionCheckboxBinding.onBind(viewHolder: RecyclerView.ViewHolder): View? = root

            override fun PermissionCheckboxBinding.onClick(
                v: View,
                position: Int,
                fastAdapter: FastAdapter<PermissionCheckboxViewBinding>,
                item: PermissionCheckboxViewBinding
            ) {
                KL.d { "Perm Click" }
                with(v.context) {
                    kauRequestPermissions(item.data.permission) { granted, _ ->
                        toast("${item.data.permission} enabled: $granted")
                        fastAdapter.notifyAdapterDataSetChanged()
                    }
                }
            }
        }
    }
}
