package ca.allanwang.kau.sample

import android.support.test.espresso.Espresso.onData
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.filters.MediumTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import org.hamcrest.Matchers.anything
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Created by Allan Wang on 22/02/2018.
 */
@RunWith(AndroidJUnit4::class)
@MediumTest
class ColorPickerTest {

    @get:Rule
    val activity: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    @Test
    fun test() {
        onView(withText(R.string.accent_color)).perform(click())
        val colors = onData(anything()).inAdapterView(withId(R.id.md_grid))
        fun click(position: Int) = colors.atPosition(0).perform(click())

        Thread.sleep(1000)
    }


}
