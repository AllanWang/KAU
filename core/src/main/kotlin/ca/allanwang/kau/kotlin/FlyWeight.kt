package ca.allanwang.kau.kotlin

/**
 * Created by Allan Wang on 26/12/17.
 *
 * Kotlin implementation of a flyweight design pattern
 * Values inside the map will be returned directly
 * Otherwise, it will be created with the supplier, saved, and returned
 * In the event a default is provided, the default takes precedence
 */
fun <K : Any, V : Any> flyweight(creator: (K) -> V) = FlyWeight(creator)

class FlyWeight<K : Any, V : Any>(private val creator: (key: K) -> V) : Map<K, V> {

    private val map = mutableMapOf<K, V>()

    override val entries: Set<Map.Entry<K, V>>
        get() = map.entries
    override val keys: Set<K>
        get() = map.keys
    override val size: Int
        get() = map.size
    override val values: Collection<V>
        get() = map.values

    override fun containsKey(key: K) = map.containsKey(key)

    override fun containsValue(value: V) = map.containsValue(value)

    override fun getOrDefault(key: K, defaultValue: V) = map.getOrDefault(key, defaultValue)

    override fun isEmpty() = map.isEmpty()

    override fun get(key: K): V {
        if (!map.containsKey(key))
            map.put(key, creator(key))
        return map[key]!!
    }
}