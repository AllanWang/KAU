package ca.allanwang.kau.kotlin

import org.jetbrains.anko.doAsync
import org.junit.Test
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.assertTrue

/**
 * Created by Allan Wang on 2017-08-06.
 */
class ZipTest {

    val debug = false

    fun p(text: String) {
        if (debug) println(text)
    }

    @Test
    fun basic() {
        val start = System.currentTimeMillis()
        val latch = CountDownLatch(1)
        val rnd = Random()
        (0..10).map {
            {
                callback: ZipCallback<Int> ->
                doAsync {
                    val sleepTime = rnd.nextInt(100) + 200L
                    p("Task $it will sleep for ${sleepTime}ms")
                    Thread.sleep(sleepTime)
                    val finish = System.currentTimeMillis()
                    p("Task $it finished in ${finish - start}ms at $finish")
                    callback(it)
                }; Unit
            }
        }.zip(-1) {
            results ->
            val finish = System.currentTimeMillis()
            println("Results ${results.contentToString()} received in ${finish - start}ms at $finish")
            assertTrue((0..10).toList().toTypedArray().contentEquals(results), "Basic zip results do not match")
            assertTrue(finish - start < 1000L, "Basic zip does not seem to be running asynchronously")
            latch.countDown()

        }
        latch.await(1100, TimeUnit.MILLISECONDS)
    }

    @Test
    fun basicAsync() {
        val start = System.currentTimeMillis()
        val latch = CountDownLatch(1)
        val rnd = Random()
        (0..10).map {
            {
                val sleepTime = rnd.nextInt(100) + 200L
                p("Task $it will sleep for ${sleepTime}ms")
                Thread.sleep(sleepTime)
                val finish = System.currentTimeMillis()
                p("Task $it finished in ${finish - start}ms at $finish")
            }
        }.zipAsync {
            val finish = System.currentTimeMillis()
            println("Results received in ${finish - start}ms at $finish")
            assertTrue(finish - start < 1000L, "BasicAsync does not seem to be wrapping the tasks asynchronously")
            latch.countDown()
        }
        latch.await(1100, TimeUnit.MILLISECONDS)
    }

}