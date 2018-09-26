package ca.allanwang.kau.kpref

import ca.allanwang.kau.kotlin.ILazyResettable


fun KPref.kpref(key: String, fallback: Boolean, postSetter: (value: Boolean) -> Unit = {}) = KPrefDelegate(key, fallback, this, postSetter)
fun KPref.kpref(key: String, fallback: Double, postSetter: (value: Float) -> Unit = {}) = KPrefDelegate(key, fallback.toFloat(), this, postSetter)
fun KPref.kpref(key: String, fallback: Float, postSetter: (value: Float) -> Unit = {}) = KPrefDelegate(key, fallback, this, postSetter)
fun KPref.kpref(key: String, fallback: Int, postSetter: (value: Int) -> Unit = {}) = KPrefDelegate(key, fallback, this, postSetter)
fun KPref.kpref(key: String, fallback: Long, postSetter: (value: Long) -> Unit = {}) = KPrefDelegate(key, fallback, this, postSetter)
fun KPref.kpref(key: String, fallback: Set<String>, postSetter: (value: Set<String>) -> Unit = {}) = KPrefDelegate(key, StringSet(fallback), this, postSetter)
fun KPref.kpref(key: String, fallback: String, postSetter: (value: String) -> Unit = {}) = KPrefDelegate(key, fallback, this, postSetter)

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
class KPrefDelegate<T : Any> internal constructor(
        private val key: String, private val fallback: T, private val pref: KPref, private var postSetter: (value: T) -> Unit = {}, lock: Any? = null
) : ILazyResettable<T> {

    private object UNINITIALIZED

    @Volatile
    private var _value: Any = UNINITIALIZED
    private val lock = lock ?: this

    init {
        if (pref.prefMap.containsKey(key))
            throw KPrefException("$key is already used elsewhere in preference ${pref.PREFERENCE_NAME}")
        pref.prefMap.put(key, this@KPrefDelegate)
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
                    _value = when (fallback) {
                        is Boolean -> pref.sp.getBoolean(key, fallback)
                        is Float -> pref.sp.getFloat(key, fallback)
                        is Int -> pref.sp.getInt(key, fallback)
                        is Long -> pref.sp.getLong(key, fallback)
                        is StringSet -> StringSet(pref.sp.getStringSet(key, fallback))
                        is String -> pref.sp.getString(key, fallback)
                        else -> throw KPrefException(fallback)
                    }
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
        when (t) {
            is Boolean -> editor.putBoolean(key, t)
            is Float -> editor.putFloat(key, t)
            is Int -> editor.putInt(key, t)
            is Long -> editor.putLong(key, t)
            is StringSet -> editor.putStringSet(key, t)
            is String -> editor.putString(key, t)
            else -> throw KPrefException(t)
        }
        editor.apply()
        postSetter(t)
    }
}

class KPrefException(message: String) : IllegalAccessException(message) {
    constructor(element: Any?) : this("Invalid type in pref cache: ${element?.javaClass?.simpleName
            ?: "null"}")
}