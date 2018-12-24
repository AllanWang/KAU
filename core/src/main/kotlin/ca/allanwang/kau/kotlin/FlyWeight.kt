/*
 * Copyright 2018 Allan Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
