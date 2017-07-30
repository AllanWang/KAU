package ca.allanwang.kau.kotlin

import java.io.Serializable
import java.lang.ref.WeakReference
import kotlin.reflect.KProperty

/**
 * Created by Allan Wang on 2017-05-30.
 *
 * Lazy delegate that can be invalidated if needed
 * https://stackoverflow.com/a/37294840/4407321
 */
internal object UNINITIALIZED

fun <T : Any> lazyResettable(initializer: () -> T): LazyResettable<T> = LazyResettable<T>(initializer)

open class LazyResettable<T : Any>(private val initializer: () -> T, lock: Any? = null) : ILazyResettable<T>, Serializable {
    @Volatile private var _value: Any = UNINITIALIZED
    private val lock = lock ?: this

    override fun invalidate() {
        _value = UNINITIALIZED
    }

    override val value: T
        get() {
            val _v1 = _value
            if (_v1 !== UNINITIALIZED)
                @Suppress("UNCHECKED_CAST")
                return _v1 as T

            return synchronized(lock) {
                val _v2 = _value
                if (_v2 !== UNINITIALIZED) {
                    @Suppress("UNCHECKED_CAST")
                    _v2 as T
                } else {
                    val typedValue = initializer()
                    _value = typedValue
                    typedValue
                }
            }
        }

    override fun isInitialized(): Boolean = _value !== UNINITIALIZED

    override fun toString(): String = if (isInitialized()) value.toString() else "Lazy value not initialized yet."

    operator fun setValue(any: Any, property: KProperty<*>, t: T) {
        _value = t
    }
}

interface ILazyResettable<T> : Lazy<T> {
    fun invalidate()
}

/*
 * The following below is a variant where lazy resettables are automatically registered to a class
 * This enables all lazy delegates to be invalidated at once
 */

interface LazyResettableRegistry {
    fun invalidateLazyResettables()
    fun <T : Any> lazyResettableRegistered(initializer: () -> T): LazyResettable<T>
}

class LazyResettableRegistryDelegate : LazyResettableRegistry {

    var lazyRegistry: WeakReference<MutableList<LazyResettable<*>>> = WeakReference(mutableListOf())

    override fun <T : Any> lazyResettableRegistered(initializer: () -> T): LazyResettable<T> {
        val lazy = lazyResettable(initializer)
       if (lazyRegistry.get() == null)
           lazyRegistry = WeakReference(mutableListOf())
        lazyRegistry.get()!!.add(lazy)
        return lazy
    }

    override fun invalidateLazyResettables() {
        lazyRegistry.get()?.forEach { it.invalidate() }
    }

}