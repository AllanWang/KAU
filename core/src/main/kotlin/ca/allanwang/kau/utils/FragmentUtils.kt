package ca.allanwang.kau.utils

import androidx.fragment.app.Fragment
import org.jetbrains.anko.bundleOf

/**
 * Created by Allan Wang on 2017-07-02.
 */
fun <T : Fragment> T.withArguments(vararg params: Pair<String, Any>): T {
    arguments = bundleOf(*params)
    return this
}