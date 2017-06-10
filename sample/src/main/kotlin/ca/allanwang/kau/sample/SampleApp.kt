package ca.allanwang.kau.sample

import android.app.Application
import timber.log.Timber

/**
 * Created by Allan Wang on 2017-06-08.
 */
class SampleApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        KPrefSample.initialize(this, "pref_sample")
    }
}