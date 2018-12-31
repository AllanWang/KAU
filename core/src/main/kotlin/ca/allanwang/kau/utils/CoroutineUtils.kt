package ca.allanwang.kau.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.android.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

object ContextHelper : CoroutineScope {

    val looper = Looper.getMainLooper()

    val handler = Handler(looper)

    /**
     * Creating dispatcher from main handler to avoid IO
     * See https://github.com/Kotlin/kotlinx.coroutines/issues/878
     */
    val dispatcher = handler.asCoroutineDispatcher("kau-main")

    override val coroutineContext: CoroutineContext get() = dispatcher
}

/**
 * Most context items implement [CoroutineScope] by default.
 * We will add a fallback just in case.
 * It is expected that the scope returned always has the Android main dispatcher as part of the context.
 */
internal inline val Context.ctxCoroutine: CoroutineScope
    get() = this as? CoroutineScope ?: ContextHelper

/**
 * Calls [launch] with an explicit dispatcher for Android's main thread
 */
fun CoroutineScope.launchMain(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
) = launch(ContextHelper.dispatcher + context, start, block)

/**
 * Calls [async] with an explicit dispatcher for Android's main thread
 */
fun CoroutineScope.asyncMain(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
) = async(ContextHelper.dispatcher + context, start, block)

/**
 * Calls [withContext] with an explicit dispatcher for Android's main thread
 */
suspend fun <T> withMainContext(block: suspend CoroutineScope.() -> T) =
    withContext(ContextHelper.dispatcher, block)
