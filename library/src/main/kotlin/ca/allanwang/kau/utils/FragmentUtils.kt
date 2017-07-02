package ca.allanwang.kau.utils

import android.support.v4.app.Fragment
import org.jetbrains.anko.bundleOf

/**
 * Created by Allan Wang on 2017-07-02.
 */
fun Fragment.withArguments(vararg params:Pair<String, Any>):Fragment {
    arguments = bundleOf(*params)
    return this
}