package ca.allanwang.kau.sample

import android.app.Application

/**
 * Created by Allan Wang on 2017-06-08.
 */
class SampleApp : Application() {
    override fun onCreate() {
        super.onCreate()
        KPrefSample.initialize(this, "pref_sample")
    }
}