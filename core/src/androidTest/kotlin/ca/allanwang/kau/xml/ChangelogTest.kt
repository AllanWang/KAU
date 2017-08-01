package ca.allanwang.kau.xml

import android.support.test.InstrumentationRegistry
import android.support.test.filters.MediumTest
import android.support.test.runner.AndroidJUnit4
import ca.allanwang.kau.test.R
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by Allan Wang on 2017-07-31.
 */
@RunWith(AndroidJUnit4::class)
@MediumTest
class ChangelogTest {

    @Test
    fun simpleTest() {
        val data = parse(InstrumentationRegistry.getTargetContext(), R.xml.text_changelog)
    }

}