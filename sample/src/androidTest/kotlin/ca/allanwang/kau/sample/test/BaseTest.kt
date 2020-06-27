package ca.allanwang.kau.sample.test

import dagger.hilt.android.testing.HiltAndroidRule
import org.junit.Rule
import kotlin.test.BeforeTest

abstract class BaseTest {
    @Suppress("LeakingThis")
    @get:Rule
    val hiltRule: HiltAndroidRule =
        HiltAndroidRule(this)

    @BeforeTest
    fun before() {
        hiltRule.inject()
    }
}