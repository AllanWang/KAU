package ca.allanwang.kau.sample

import android.app.Application
import ca.allanwang.kau.logging.KL

/**
 * Created by Allan Wang on 2017-06-08.
 */
class SampleApp : Application() {
    override fun onCreate() {
        super.onCreate()
        KL.enabled = BuildConfig.DEBUG
        KPrefSample.initialize(this, "pref_sample")
    }
}