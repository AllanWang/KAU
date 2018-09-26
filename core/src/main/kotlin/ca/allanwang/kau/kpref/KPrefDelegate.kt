package ca.allanwang.kau.kpref

import ca.allanwang.kau.kotlin.ILazyResettable


fun KPref.kpref(key: String, fallback: Boolean, postSetter: (value: Boolean) -> Unit = {}) =
        KPrefDelegate(key, fallback, this, KPrefBooleanTransaction, postSetter)

fun KPref.kpref(key: String, fallback: Float, postSetter: (value: Float) -> Unit = {}) =
        KPrefDelegate(key, fallback, this, KPrefFloatTransaction, postSetter)

@Deprecated("Double is not supported in SharedPreferences; cast to float yourself",
        ReplaceWith("kpref(key, fallback.toFloat(), postSetter)"),
        DeprecationLevel.WARNING)
fun KPref.kpref(key: String, fallback: Double, postSetter: (value: Float) -> Unit = {}) =
        kpref(key, fallback.toFloat(), postSetter)

fun KPref.kpref(key: String, fallback: Int, postSetter: (value: Int) -> Unit = {}) =
        KPrefDelegate(key, fallback, this, KPrefIntTransaction, postSetter)

fun KPref.kpref(key: String, fallback: Long, postSetter: (value: Long) -> Unit = {}) =
        KPrefDelegate(key, fallback, this, KPrefLongTransaction, postSetter)

fun KPref.kpref(key: String, fallback: Set<String>?, postSetter: (value: Set<String>) -> Unit = {}) =
        KPrefDelegate(key, fallback, this, KPrefSetTransaction) { postSetter(it ?: emptySet()) }

fun KPref.kpref(key: String, fallback: String, postSetter: (value: String) -> Unit = {}) =
        KPrefDelegate(key, fallback, this, KPrefStringTransaction, postSetter)

/**
 * Created by Allan Wang on 2017-06-07.
 *
 * Implementation of a kpref data item
 * Contains a unique key for the shared preference as well as a nonnull fallback item
 * Also contains an optional mutable postSetter that will be called every time a new value is given
 */
class KPrefDelegate<T> internal constructor(
        private val key: String,
        private val fallback: T,
        private val pref: KPref,
        private val transaction: KPrefTransaction<T>,
        private var postSetter: (value: T) -> Unit = {}
) : ILazyResettable<T> {

    private object UNINITIALIZED

    @Volatile
    private var _value: Any? = UNINITIALIZED
    private val lock = this

    init {
        if (pref.prefMap.containsKey(key))
            throw KPrefException("$key is already used elsewhere in preference ${pref.PREFERENCE_NAME}")
        pref.prefMap[key] = this@KPrefDelegate
    }

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
                    _value = transaction.get(pref.sp, key, fallback)
                    @Suppress("UNCHECKED_CAST")
                    _value as T
                }
            }
        }

    override fun isInitialized(): Boolean = _value !== UNINITIALIZED

    override fun toString(): String = if (isInitialized()) value.toString() else "Lazy kPref $key not initialized yet."

    operator fun setValue(any: Any, property: kotlin.reflect.KProperty<*>, t: T) {
        _value = t
        val editor = pref.sp.edit()
        transaction.set(editor, key, t)
        editor.apply()
        postSetter(t)
    }
}

class KPrefException(message: String) : IllegalAccessException(message)