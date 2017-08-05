package ca.allanwang.kau.kotlin

import org.junit.Test
import kotlin.test.assertEquals

/**
 * Created by Allan Wang on 2017-08-05.
 *
 * Test code for [kauRemoveIf]
 */
class StreamsTest {

    @Test
    fun basic() {
        val items = mutableListOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
        items.kauRemoveIf { it % 2 == 0 }
        assertEquals(listOf(1, 3, 5, 7, 9), items)
    }

    @Test
    fun objectReference() {
        data class Potato(val id: Int)

        val thePotato = Potato(9)
        val items = mutableListOf<Potato>()
        val result = mutableListOf<Potato>()
        for (i in 0..11) {
            val potato = Potato(i)
            items.add(potato)
            result.add(potato)
        }
        items.add(3, thePotato)
        assertEquals(result.size + 1, items.size, "Invalid list addition")
        assertEquals(2, items.filter { it.id == 9 }.size, "Invalid number of potatoes with id 9")
        items.kauRemoveIf { it === thePotato } //removal by reference
        assertEquals(result.size, items.size, "Invalid list size after removal")
        assertEquals(result, items)
        items.kauRemoveIf { it == thePotato } //removal by equality
        assertEquals(result.size - 1, items.size, "Invalid list removal based on equality")
    }

}