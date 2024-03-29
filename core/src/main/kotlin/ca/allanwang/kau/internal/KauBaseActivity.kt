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
package ca.allanwang.kau.internal

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ca.allanwang.kau.permissions.kauOnRequestPermissionsResult
import ca.allanwang.kau.utils.ContextHelper
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob

/**
 * Created by Allan Wang on 2017-08-01.
 *
 * Base activity for any activity that would have extended [AppCompatActivity]
 *
 * Ensures that some singleton methods are called. This is simply a convenience class; you can
 * always copy and paste this to your own class.
 *
 * This also implements [CoroutineScope] that adheres to the activity lifecycle. Note that by
 * default, [SupervisorJob] is used, to avoid exceptions in one child from affecting that of
 * another. The default job can be overridden within [defaultJob]
 */
abstract class KauBaseActivity : AppCompatActivity(), CoroutineScope {

  open lateinit var job: Job
  override val coroutineContext: CoroutineContext
    get() = ContextHelper.dispatcher + job

  open fun defaultJob(): Job = SupervisorJob()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    job = defaultJob()
  }

  override fun onDestroy() {
    job.cancel()
    super.onDestroy()
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    kauOnRequestPermissionsResult(permissions, grantResults)
  }
}
