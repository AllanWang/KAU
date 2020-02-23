package ca.allanwang.kau.sample

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import ca.allanwang.kau.kpref.KPrefFactory
import ca.allanwang.kau.kpref.KPrefFactoryInMemory
import ca.allanwang.kau.logging.KL
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.get
import org.koin.dsl.module

class SampleTestRunner : AndroidJUnitRunner() {
    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(cl, SampleTestApp::class.java.name, context)
    }
}

class SampleTestRule : TestRule {
    override fun apply(base: Statement, description: Description): Statement =
        object : Statement(), KoinComponent {
            override fun evaluate() {

                // Reset prefs
                val pref: KPrefSample = get()
                pref.reset()

                base.evaluate()
            }
        }
}

class SampleTestApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            if (BuildConfig.DEBUG) {
                androidLogger()
            }
            androidContext(this@SampleTestApp)
            modules(listOf(prefFactoryModule(), SampleApp.prefModule()))
        }
    }

    fun prefFactoryModule() = module {
        single<KPrefFactory> {
            KPrefFactoryInMemory
        }
    }
}