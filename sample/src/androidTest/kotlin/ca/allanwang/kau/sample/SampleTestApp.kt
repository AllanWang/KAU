/*
 * Copyright 2020 Allan Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ca.allanwang.kau.sample

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.lang.RuntimeException

class SampleTestRunner : AndroidJUnitRunner() {
    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}

class SampleTestRule : TestRule {

    @EntryPoint
    @InstallIn(ApplicationComponent::class)
    interface SampleTestRuleEntryPoint {
        fun pref(): KPrefSample
    }

    override fun apply(base: Statement, description: Description): Statement =
        object : Statement() {
            override fun evaluate() {
                val entryPoint = EntryPointAccessors.fromApplication(
                    ApplicationProvider.getApplicationContext(),
                    SampleTestRuleEntryPoint::class.java
                )

                // Reset prefs
                entryPoint.pref().reset()

                base.evaluate()
            }
        }
}
