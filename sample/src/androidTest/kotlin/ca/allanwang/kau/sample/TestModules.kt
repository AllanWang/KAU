package ca.allanwang.kau.sample

import ca.allanwang.kau.kpref.KPrefFactory
import ca.allanwang.kau.kpref.KPrefFactoryInMemory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@Module
@InstallIn(ApplicationComponent::class)
object PrefFactoryTestModule {
    @Provides
    fun factory(): KPrefFactory = KPrefFactoryInMemory
}