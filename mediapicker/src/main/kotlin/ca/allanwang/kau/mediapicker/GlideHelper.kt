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
package ca.allanwang.kau.mediapicker

import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager

/**
 * Created by Allan Wang on 29/08/2017.
 *
 * Basic helper to fetch the [RequestManager] from the activity if it exists, before creating
 * another one
 */
internal interface GlideContract {
  fun glide(v: View): RequestManager
}

internal class GlideDelegate : GlideContract {
  override fun glide(v: View) = ((v.context as? MediaPickerCore<*>)?.glide ?: Glide.with(v))!!
}
