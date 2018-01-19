package ca.allanwang.kau.about

import android.content.Context
import ca.allanwang.kau.utils.startActivity
import ca.allanwang.kau.utils.withSceneTransitionAnimation

/**
 * Created by Allan Wang on 2017-07-22.
 */

/**
 * About activity launcher
 */
inline fun <reified T : AboutActivityBase> Context.kauLaunchAbout() =
        startActivity<T>(bundleBuilder = {
            withSceneTransitionAnimation(this@kauLaunchAbout)
        })