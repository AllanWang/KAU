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
package ca.allanwang.kau.utils

import androidx.customview.widget.ViewDragHelper

/**
 * Created by Allan Wang on 2017-06-08.
 */
const val ANDROID_NAMESPACE = "http://schemas.android.com/apk/res/android"

const val KAU_LEFT = ViewDragHelper.EDGE_LEFT
const val KAU_RIGHT = ViewDragHelper.EDGE_RIGHT
const val KAU_TOP = ViewDragHelper.EDGE_TOP
const val KAU_BOTTOM = ViewDragHelper.EDGE_BOTTOM
const val KAU_HORIZONTAL = KAU_LEFT or KAU_RIGHT
const val KAU_VERTICAL = KAU_TOP or KAU_BOTTOM
const val KAU_ALL = KAU_HORIZONTAL or KAU_VERTICAL

const val KAU_COLLAPSED = 0
const val KAU_COLLAPSING = 1
const val KAU_EXPANDING = 2
const val KAU_EXPANDED = 3

const val KAU_ELLIPSIS = '\u2026'
