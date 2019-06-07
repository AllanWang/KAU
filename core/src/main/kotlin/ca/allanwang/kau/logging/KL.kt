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
package ca.allanwang.kau.logging

import ca.allanwang.kau.BuildConfig

/**
 * Created by Allan Wang on 2017-06-19.
 *
 * Internal KAU logger
 */
object KL : KauLogger("KAU", { BuildConfig.DEBUG }) {

    /**
     * Logger with searchable tag and thread info
     */
    inline fun test(message: () -> Any?) {
        if (BuildConfig.DEBUG)
            d { "Test1234 ${Thread.currentThread().name} ${message()}" }
    }
}
