package ca.allanwang.kau.kotlin

/**
 * Shortcut for unsynchronized lazy block
 */
fun <T> lazyUi(initializer: () -> T): Lazy<T> = lazy(LazyThreadSafetyMode.NONE, initializer)