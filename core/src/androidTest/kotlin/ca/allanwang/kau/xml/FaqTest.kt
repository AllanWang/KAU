package ca.allanwang.kau.xml

import androidx.test.InstrumentationRegistry
import androidx.test.filters.MediumTest
import androidx.test.runner.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals

/**
 * Created by Allan Wang on 2017-08-01.
 */
@RunWith(AndroidJUnit4::class)
@MediumTest
class FaqTest {

    @Test
    fun simpleTest() {
        InstrumentationRegistry.getTargetContext().kauParseFaq(R.xml.test_faq) { data ->
            assertEquals(2, data.size, "FAQ size is incorrect")
            assertEquals("1. This is a question", data.first().question.toString(), "First question does not match")
            assertEquals("This is an answer", data.first().answer.toString(), "First answer does not match")
            assertEquals("2. This is another question", data.last().question.toString(), "Second question does not match")
            assertEquals("This is another answer", data.last().answer.toString(), "Second answer does not match")
        }
    }

    @Test
    fun withoutNumbering() {
        InstrumentationRegistry.getTargetContext().kauParseFaq(R.xml.test_faq, false) { data ->
            assertEquals(2, data.size, "FAQ size is incorrect")
            assertEquals("This is a question", data.first().question.toString(), "First question does not match")
            assertEquals("This is an answer", data.first().answer.toString(), "First answer does not match")
            assertEquals("This is another question", data.last().question.toString(), "Second question does not match")
            assertEquals("This is another answer", data.last().answer.toString(), "Second answer does not match")
        }
    }

}