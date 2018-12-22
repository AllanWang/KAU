package ca.allanwang.kau.sample

import androidx.test.espresso.DataInteraction
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.filters.MediumTest
import androidx.test.rule.ActivityTestRule
import android.view.View
import androidx.test.ext.junit.runners.AndroidJUnit4
import ca.allanwang.kau.colorpicker.CircleView
import org.hamcrest.Matchers.anything
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.fail


/**
 * Created by Allan Wang on 22/02/2018.
 *
 * Tests related to the :colorpicker module
 */
@RunWith(AndroidJUnit4::class)
@MediumTest
class ColorPickerTest {

    @get:Rule
    val activity: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    private fun DataInteraction.click(position: Int) =
            atPosition(position).perform(click())

    private fun View.colorSelected(selected: Boolean) {
        val circle = this as? CircleView ?: fail("View is not a CircleView")
        assertEquals(selected, circle.colorSelected, "CircleView ${circle.tag} ${if (selected) "is not" else "is"} actually selected")
    }

    private val colorSelected = ViewAssertion { view, _ -> view.colorSelected(true) }

    private val colorNotSelected = ViewAssertion { view, _ -> view.colorSelected(false) }

    @Test
    fun test() {
        onView(withText(R.string.accent_color)).perform(click())
        val colors = onData(anything()).inAdapterView(withId(R.id.md_grid))

        colors.click(0).check(colorNotSelected) // enter sub grid
        colors.click(0).check(colorSelected)    // click first grid item
        colors.atPosition(1).check(colorNotSelected)
        colors.atPosition(2).check(colorNotSelected)
                .perform(click()).check(colorSelected)
        colors.atPosition(0).check(colorNotSelected)
                .perform(click()).check(colorSelected)
        // first item is now selected
    }


}
