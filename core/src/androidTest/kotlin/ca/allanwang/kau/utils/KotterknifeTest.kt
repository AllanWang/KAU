package ca.allanwang.kau.utils

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.filters.MediumTest
import android.support.test.runner.AndroidJUnit4
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by Allan Wang on 2017-07-30.
 */
@RunWith(AndroidJUnit4::class)
@MediumTest
class KotterknifeTest {

    lateinit var context: Context

    @Before
    fun init() {
        context = InstrumentationRegistry.getContext()
    }

    @Test
    fun testCast() {
        class Example(context: Context) : FrameLayout(context) {
            val name: TextView by bindView(1)
        }

        val example = Example(context)
        example.addView(textViewWithId(1))
        assertNotNull(example.name)
    }

    @Test
    fun testFindCached() {
        class Example(context: Context) : FrameLayout(context) {
            val name: View by bindView(1)
        }

        val example = Example(context)
        example.addView(viewWithId(1))
        assertNotNull(example.name)
        example.removeAllViews()
        assertNotNull(example.name)
    }

    @Test
    fun testOptional() {
        class Example(context: Context) : FrameLayout(context) {
            val present: View? by bindOptionalView(1)
            val missing: View? by bindOptionalView(2)
        }

        val example = Example(context)
        example.addView(viewWithId(1))
        assertNotNull(example.present)
        assertNull(example.missing)
    }

    @Test
    fun testOptionalCached() {
        class Example(context: Context) : FrameLayout(context) {
            val present: View? by bindOptionalView(1)
            val missing: View? by bindOptionalView(2)
        }

        val example = Example(context)
        example.addView(viewWithId(1))
        assertNotNull(example.present)
        assertNull(example.missing)
        example.removeAllViews()
        example.addView(viewWithId(2))
        assertNotNull(example.present)
        assertNull(example.missing)
    }

    @Test
    fun testMissingFails() {
        class Example(context: Context) : FrameLayout(context) {
            val name: TextView? by bindView(1)
        }

        val example = Example(context)
        try {
            example.name
        } catch (e: IllegalStateException) {
            assertEquals("View ID 1 for 'name' not found.", e.message)
        }
    }

    @Test
    fun testList() {
        class Example(context: Context) : FrameLayout(context) {
            val name: List<TextView> by bindViews(1, 2, 3)
        }

        val example = Example(context)
        example.addView(viewWithId(1))
        example.addView(viewWithId(2))
        example.addView(viewWithId(3))
        assertNotNull(example.name)
        assertEquals(3, example.name.size)
    }

    @Test
    fun testListCaches() {
        class Example(context: Context) : FrameLayout(context) {
            val name: List<TextView> by bindViews(1, 2, 3)
        }

        val example = Example(context)
        example.addView(viewWithId(1))
        example.addView(viewWithId(2))
        example.addView(viewWithId(3))
        assertNotNull(example.name)
        assertEquals(3, example.name.size)
        example.removeAllViews()
        assertNotNull(example.name)
        assertEquals(3, example.name.size)
    }

    @Test
    fun testListMissingFails() {
        class Example(context: Context) : FrameLayout(context) {
            val name: List<TextView> by bindViews(1, 2, 3)
        }

        val example = Example(context)
        example.addView(viewWithId(1))
        example.addView(viewWithId(3))
        try {
            example.name
        } catch (e: IllegalStateException) {
            assertEquals("View ID 2 for 'name' not found.", e.message)
        }
    }

    @Test
    fun testOptionalList() {
        class Example(context: Context) : FrameLayout(context) {
            val name: List<TextView> by bindOptionalViews(1, 2, 3)
        }

        val example = Example(context)
        example.addView(viewWithId(1))
        example.addView(viewWithId(3))
        assertNotNull(example.name)
        assertEquals(2, example.name.size)
    }

    @Test
    fun testOptionalListCaches() {
        class Example(context: Context) : FrameLayout(context) {
            val name: List<TextView> by bindOptionalViews(1, 2, 3)
        }

        val example = Example(context)
        example.addView(viewWithId(1))
        example.addView(viewWithId(3))
        assertNotNull(example.name)
        assertEquals(2, example.name.size)
        example.removeAllViews()
        assertNotNull(example.name)
        assertEquals(2, example.name.size)
    }

    @Test
    fun testReset() {
        class Example(context: Context) : FrameLayout(context) {
            val name: View? by bindOptionalViewResettable(1)
        }

        val example = Example(context)
        example.addView(viewWithId(1))
        assertNotNull(example.name)
        example.removeAllViews()
        Kotterknife.reset(example)
        assertNull(example.name)
    }

    private fun viewWithId(id: Int): View {
        val view = View(context)
        view.id = id
        return view
    }

    private fun textViewWithId(id: Int): View {
        val view = TextView(context)
        view.id = id
        return view
    }
}