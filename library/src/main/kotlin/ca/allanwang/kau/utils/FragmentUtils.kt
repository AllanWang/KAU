package ca.allanwang.kau.utils

import android.os.Bundle
import android.support.v4.app.Fragment

/**
 * Created by Allan Wang on 2017-05-29.
 */
fun <T : Fragment> T.withBundle(builder: Bundle.() -> Unit = {}): T {
    if (this.arguments == null) this.arguments = Bundle()
    this.arguments.builder()
    return this
}