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

import android.os.Bundle
import ca.allanwang.kau.adapters.SingleFastAdapter
import ca.allanwang.kau.internal.KauBaseActivity
import ca.allanwang.kau.permissions.PERMISSION_ACCESS_COARSE_LOCATION
import ca.allanwang.kau.permissions.PERMISSION_ACCESS_FINE_LOCATION
import ca.allanwang.kau.permissions.PERMISSION_CAMERA
import ca.allanwang.kau.swipe.SWIPE_EDGE_LEFT
import ca.allanwang.kau.swipe.kauSwipeOnCreate
import ca.allanwang.kau.swipe.kauSwipeOnDestroy
import ca.allanwang.kau.utils.fullLinearRecycler
import ca.allanwang.kau.utils.startActivity
import ca.allanwang.kau.utils.withAlpha
import ca.allanwang.kau.utils.withSlideOut

/**
 * Created by Allan Wang on 2017-06-12.
 *
 * Activity for animations
 * Now also showcases permissions
 */
class AnimActivity : KauBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val adapter = SingleFastAdapter()
        setContentView(fullLinearRecycler(adapter).apply {
            setBackgroundColor(
                KPrefSample.bgColor.withAlpha(255)
            )
        })

        adapter.add(listOf(
            PERMISSION_ACCESS_COARSE_LOCATION,
            PERMISSION_ACCESS_FINE_LOCATION,
            PERMISSION_CAMERA
        ).map { PermissionCheckboxModel(it).vh() })
        adapter.addEventHook(PermissionCheckboxViewBinding.clickHook())
        kauSwipeOnCreate {
            edgeFlag = SWIPE_EDGE_LEFT
        }
    }

    override fun onDestroy() {
        kauSwipeOnDestroy()
        super.onDestroy()
    }

    override fun onBackPressed() {
        startActivity<MainActivity>(bundleBuilder = {
            withSlideOut(this@AnimActivity)
        })
    }
}
