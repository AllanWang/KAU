package ca.allanwang.kau.sample.utils

import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher

fun <T> index(index: Int, matcher: Matcher<T>): Matcher<T> =
    object : BaseMatcher<T>() {

        var current = 0

        override fun describeTo(description: Description) {
            description.appendText("Should return item at index $index")
        }

        override fun matches(item: Any?): Boolean {
            println("AA")
            return matcher.matches(item) && current++ == index
        }
    }

fun <T> first(matcher: Matcher<T>): Matcher<T> = index(0, matcher)
