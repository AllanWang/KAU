package ca.allanwang.kau.sample

import ca.allanwang.kau.kpref.KPrefFactory
import ca.allanwang.kau.kpref.KPrefFactoryInMemory
import org.koin.dsl.module

object SampleTestApp {
    fun prefFactoryModule() = module {
        single<KPrefFactory> {
            KPrefFactoryInMemory
        }
    }
}