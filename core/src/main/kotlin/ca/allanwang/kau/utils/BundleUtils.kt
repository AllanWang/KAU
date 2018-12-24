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

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.os.Bundle
import android.util.Pair
import android.view.View
import androidx.annotation.AnimRes
import ca.allanwang.kau.R

/**
 * Created by Allan Wang on 10/12/17.
 */
/**
 * Similar to [Bundle.putAll], but checks for a null insert and returns the parent bundle
 */
infix fun Bundle.with(bundle: Bundle?): Bundle {
    if (bundle != null) putAll(bundle)
    return this
}

/**
 * Adds transition bundle if context is activity and build is lollipop+
 */
@SuppressLint("NewApi")
fun Bundle.withSceneTransitionAnimation(context: Context) {
    if (context !is Activity || !buildIsLollipopAndUp) return
    val options = ActivityOptions.makeSceneTransitionAnimation(context)
    putAll(options.toBundle())
}

/**
 * Given the parent view and map of view ids to tags,
 * create a scene transition animation
 */
fun Bundle.withSceneTransitionAnimation(parent: View, data: Map<Int, String>) =
    withSceneTransitionAnimation(parent.context, data.mapKeys { (id, _) ->
        parent.findViewById<View>(id)
    })

/**
 * Given a mapping of views to tags,
 * create a scene transition animation
 */
@SuppressLint("NewApi")
fun Bundle.withSceneTransitionAnimation(context: Context, data: Map<View, String>) {
    if (context !is Activity || !buildIsLollipopAndUp) return
    val options = ActivityOptions.makeSceneTransitionAnimation(context,
        *data.map { (view, tag) -> Pair(view, tag) }.toTypedArray()
    )
    putAll(options.toBundle())
}

fun Bundle.withCustomAnimation(
    context: Context,
    @AnimRes enterResId: Int,
    @AnimRes exitResId: Int
) {
    this with ActivityOptions.makeCustomAnimation(
        context,
        enterResId, exitResId
    ).toBundle()
}

fun Bundle.withSlideIn(context: Context) = withCustomAnimation(
    context,
    R.anim.kau_slide_in_right, R.anim.kau_fade_out
)

fun Bundle.withSlideOut(context: Context) = withCustomAnimation(
    context,
    R.anim.kau_fade_in, R.anim.kau_slide_out_right_top
)

fun Bundle.withFade(context: Context) = withCustomAnimation(
    context,
    android.R.anim.fade_in, android.R.anim.fade_out
)
