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
package ca.allanwang.kau.swipe

interface SwipeListener {
    /**
     * Invoked as the page is scrolled
     * Percent is capped at 1.0, even if there is a slight overscroll for the pages
     */
    fun onScroll(percent: Float, px: Int, edgeFlag: Int)

    /**
     * Invoked when page first consumes the scroll events
     */
    fun onEdgeTouch()

    /**
     * Invoked when scroll percent over the threshold for the first time
     */
    fun onScrollToClose(edgeFlag: Int)
}
