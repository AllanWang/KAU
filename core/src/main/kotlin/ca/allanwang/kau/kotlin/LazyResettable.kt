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

interface ILazyResettableRegistry {
    fun <T : Any> lazy(initializer: () -> T): LazyResettable<T>
    fun <T : Any> add(resettable: LazyResettable<T>): LazyResettable<T>
    /**
     * Removes duplicates from our registry
     */
    fun clean()
    fun invalidateLazyResettables()
}

/**
 * The following below is a helper class that registers all resettables into a weakly held list
 * All resettables can therefore be invalidated at once
 */
class LazyResettableRegistry : ILazyResettableRegistry {

    private var lazyRegistry: WeakReference<MutableList<LazyResettable<*>>> = WeakReference(mutableListOf())

    //ensure that our list is valid
    private val registryList: MutableList<LazyResettable<*>>
        get() {
            if (lazyRegistry.get() == null)
                lazyRegistry = WeakReference(mutableListOf())
            return lazyRegistry.get()!!
        }

    override fun <T : Any> lazy(initializer: () -> T): LazyResettable<T> {
        val lazy = lazyResettable(initializer)
        registryList.add(lazy)
        return lazy
    }

    override fun <T : Any> add(resettable: LazyResettable<T>): LazyResettable<T> {
        if (!registryList.contains(resettable))
            registryList.add(resettable)
        return resettable
    }

    override fun invalidateLazyResettables() {
        lazyRegistry.get()?.forEach { it.invalidate() }
    }

    override fun clean() {
        lazyRegistry = WeakReference(registryList.toSet().toMutableList())
    }
}

fun <T : Any> lazyResettable(initializer: () -> T, registry: ILazyResettableRegistry): LazyResettable<T> {
    val lazy = LazyResettable<T>(initializer)
    registry.add(lazy)
    return lazy
}