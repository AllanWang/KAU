/*
 * Copyright 2018 Allan Wang
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

import android.view.View
import android.widget.CheckBox
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.withChild
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.instanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import javax.inject.Inject

/**
 * Created by Allan Wang on 21/12/2018.
 *
 * Tests related to the :kpref-activity module
 */
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
@UninstallModules(PrefFactoryModule::class)
class KPrefViewTest : BaseTest() {

    val activity: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    @get:Rule
    val rule: TestRule = RuleChain.outerRule(SampleTestRule()).around(activity)

    @Inject lateinit var pref: KPrefSample

    fun verifyCheck(checked: Boolean): Matcher<View> {
        return object : BoundedMatcher<View, View>(View::class.java) {

            override fun describeTo(description: Description) {
                description.appendText("Checkbox is ${if (checked) "checked" else "not checked"}")
            }

            override fun matchesSafely(item: View): Boolean =
                item.findViewById<CheckBox>(R.id.kau_pref_inner_content).isChecked == checked
        }
    }

    inline fun <reified T : View> ViewInteraction.checkInnerContent(
        desc: String,
        crossinline matcher: (T) -> Boolean
    ): ViewInteraction {
        val viewMatcher = object : BaseMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText(desc)
            }

            override fun matches(item: Any?): Boolean {
                val view = item as? View ?: return false
                val inner = view.findViewById<View>(R.id.kau_pref_inner_content) as? T
                    ?: return false
                return matcher(inner)
            }
        }
        return check(matches(viewMatcher))
    }

    fun ViewInteraction.verifyCheck(tag: String, checked: Boolean, enabled: Boolean = true) =
        checkInnerContent<CheckBox>("$tag should be ${if (checked) "checked" else "not checked"}") {
            it.isChecked == checked
        }.check { view, _ ->
            ((view.alpha == 1f) == enabled)
        }

    fun onCheckboxView(vararg matchers: Matcher<View>) =
        onView(allOf(*matchers, withChild(withChild(instanceOf(CheckBox::class.java)))))

    @Test
    fun basicCheckboxToggle() {
        val checkbox1 = onCheckboxView(withChild(withText(R.string.checkbox_1)))

        assertTrue(pref.check1, "check1 not normalized")

        checkbox1.verifyCheck("checkbox1 init", true)
        checkbox1.perform(click())
        checkbox1.verifyCheck("checkbox1 after click", false)
    }

    /**
     * Note that checkbox3 depends on checkbox2
     */
    @Test
    fun dependentCheckboxToggle() {
        val checkbox2 = onCheckboxView(withChild(withText(R.string.checkbox_2)))
        val checkbox3 =
            onCheckboxView(
                withChild(withText(R.string.checkbox_3)),
                withChild(withText(R.string.desc_dependent))
            )

        assertFalse(pref.check2, "check2 not normalized")
        assertFalse(pref.check3, "check3 not normalized")

        checkbox2.verifyCheck("checkbox2 init", checked = false, enabled = true)
        checkbox3.verifyCheck("checkbox3 init", checked = false, enabled = false)
        checkbox3.perform(click())
        checkbox3.verifyCheck("checkbox3 after disabled click", checked = false, enabled = false)

        checkbox2.perform(click())
        checkbox2.verifyCheck("checkbox2 after click", checked = true, enabled = true)
        checkbox3.verifyCheck("checkbox3 after checkbox2 click", checked = false, enabled = true)

        checkbox3.perform(click())
        checkbox3.verifyCheck("checkbox3 after enabled click", checked = true, enabled = true)
    }
}
