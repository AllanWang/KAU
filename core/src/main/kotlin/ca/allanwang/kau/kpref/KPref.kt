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
package ca.allanwang.kau.kpref

import ca.allanwang.kau.kotlin.ILazyResettable

/**
 * Created by Allan Wang on 2017-06-07.
 *
 * Base class for shared preferences All objects extending this class must be called in the app's
 * [android.app.Application] class
 *
 * See the [KPref.kpref] extensions for more details
 *
 * Furthermore, all kprefs are held in the [prefMap], so if you wish to reset a preference, you must
 * also invalidate the kpref from that map
 */
open class KPref private constructor(val preferenceName: String, val builder: KPrefBuilder) :
    KPrefBuilder by builder {

  constructor(
      preferenceName: String,
      factory: KPrefFactory
  ) : this(preferenceName, factory.createBuilder(preferenceName))

  internal val prefMap: MutableMap<String, ILazyResettable<*>> = mutableMapOf()

  fun add(entry: KPrefDelegate<*>) {
    if (prefMap.containsKey(entry.key))
        throw KPrefException("${entry.key} is already used elsewhere in preference $preferenceName")
    prefMap[entry.key] = entry
  }

  fun reset() {
    prefMap.values.forEach { it.invalidate() }
  }

  operator fun get(key: String): ILazyResettable<*>? = prefMap[key]

  /** Exposed key deletion function from builder. To avoid recursion, this type uses vararg */
  fun deleteKeys(vararg keys: String) {
    deleteKeys(keys)
  }
}
