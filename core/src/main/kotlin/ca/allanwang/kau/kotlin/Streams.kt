package ca.allanwang.kau.kotlin

/**
 * Created by Allan Wang on 2017-08-05.
 */

/**
 * Replica of [java.util.Vector.removeIf] in Java
 * Since we don't have access to the internals of our extended class,
 * We will simply iterate and remove when the filter returns {@code false}
 */
@Synchronized
inline fun <T, C : MutableIterable<T>> C.kauRemoveIf(filter: (item: T) -> Boolean): C {
    val iter = iterator()
    while (iter.hasNext())
        if (filter(iter.next())) iter.remove()
    return this
}

/**
 * Returns the first element tha matches the predicate,
 * or null if no match is found
 */
inline fun <T : Any> Iterator<T>.firstOrNull(predicate: (T) -> Boolean): T? {
    while (hasNext()) {
        val data = next()
        if (predicate(data)) return data
    }
    return null
}