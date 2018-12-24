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
import androidx.test.filters.MediumTest
import androidx.test.rule.ActivityTestRule
import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.instanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by Allan Wang on 21/12/2018.
 *
 * Tests related to the :kpref-activity module
 */
@RunWith(AndroidJUnit4::class)
@MediumTest
class KPrefViewTest {

    @get:Rule
    val activity: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

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

        val initiallyChecked = KPrefSample.check1

        checkbox1.verifyCheck("checkbox1 init", initiallyChecked)
        checkbox1.perform(click())
        checkbox1.verifyCheck("checkbox1 after click", !initiallyChecked)
    }

    /**
     * Note that checkbox3 depends on checkbox2
     */
    @Test
    fun dependentCheckboxToggle() {
        val checkbox2 = onCheckboxView(withChild(withText(R.string.checkbox_2)))
        val checkbox3 =
            onCheckboxView(withChild(withText(R.string.checkbox_3)), withChild(withText(R.string.desc_dependent)))

        // normalize so that both are checked
        if (!KPrefSample.check2)
            checkbox2.perform(click())
        if (!KPrefSample.check3)
            checkbox3.perform(click())

        checkbox3.verifyCheck("checkbox3 init", true, true)
        checkbox3.perform(click())
        checkbox3.verifyCheck("checkbox3 after click", false, true)

        checkbox2.perform(click())
        checkbox2.verifyCheck("checkbox2 after click", false, true)
        checkbox3.verifyCheck("checkbox3 after checkbox2 click", false, false)

        checkbox3.perform(click())
        checkbox3.verifyCheck("checkbox3 after disabled click", false, false)
    }
}
