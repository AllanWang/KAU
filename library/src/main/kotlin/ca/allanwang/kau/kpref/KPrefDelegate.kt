package ca.allanwang.kau.kpref

/**
 * Created by Allan Wang on 2017-06-07.
 */
private object UNINITIALIZED

fun KPref.kpref(key: String, fallback: Boolean): KPrefDelegate<Boolean> = KPrefDelegate(key, fallback, this)
fun KPref.kpref(key: String, fallback: Double): KPrefDelegate<Float> = KPrefDelegate(key, fallback.toFloat(), this)
fun KPref.kpref(key: String, fallback: Float): KPrefDelegate<Float> = KPrefDelegate(key, fallback, this)
fun KPref.kpref(key: String, fallback: Int): KPrefDelegate<Int> = KPrefDelegate(key, fallback, this)
fun KPref.kpref(key: String, fallback: Long): KPrefDelegate<Long> = KPrefDelegate(key, fallback, this)
fun KPref.kpref(key: String, fallback: Set<String>): KPrefDelegate<StringSet> = KPrefDelegate(key, StringSet(fallback), this)
fun KPref.kpref(key: String, fallback: String): KPrefDelegate<String> = KPrefDelegate(key, fallback, this)

class StringSet(set: Collection<String>) : LinkedHashSet<String>(set)

class KPrefDelegate<T : Any> internal constructor(private val key: String, private val fallback: T, private val pref: KPref, lock: Any? = null) : Lazy<T>, java.io.Serializable {

    @Volatile private var _value: Any = UNINITIALIZED
    private val lock = lock ?: this

    init {
        if (pref.prefMap.containsKey(key))
            throw KPrefException("$key is already used elsewhere in preference ${pref.PREFERENCE_NAME}")
        pref.prefMap.put(key, this@KPrefDelegate)
    }

    fun invalidate() {
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
                        is StringSet -> pref.sp.getStringSet(key, fallback)
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
    }
}

class KPrefException(message: String) : IllegalAccessException(message) {
    constructor(element: Any?) : this("Invalid type in pref cache: ${element?.javaClass?.simpleName ?: "null"}")
}