@file:Suppress("UNCHECKED_CAST", "DEPRECATION")

package ca.allanwang.kau.utils

/**
 * Created by Allan Wang on 2017-05-29.
 *
 * Courtesy of Jake Wharton
 *
 * https://github.com/JakeWharton/kotterknife/blob/master/src/main/kotlin/kotterknife/ButterKnife.kt
 *
 * Note that while this is useful for binding ids, there also exists other alternatives, such as
 * `kotlin-android-extensions`.
 *
 * For fragments, make sure that the views are reset after the fragment lifecycle.
 */
import android.app.Activity
import android.app.Dialog
import android.app.DialogFragment
import android.app.Fragment
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import java.util.Collections
import java.util.WeakHashMap
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty
import androidx.fragment.app.DialogFragment as SupportDialogFragment
import androidx.fragment.app.Fragment as SupportFragment

fun <V : View> View.bindView(id: Int)
    : ReadOnlyProperty<View, V> = required(id, viewFinder)

fun <V : View> Activity.bindView(id: Int)
    : ReadOnlyProperty<Activity, V> = required(id, viewFinder)

fun <V : View> Dialog.bindView(id: Int)
    : ReadOnlyProperty<Dialog, V> = required(id, viewFinder)

fun <V : View> DialogFragment.bindView(id: Int)
    : ReadOnlyProperty<DialogFragment, V> = required(id, viewFinder)

fun <V : View> SupportDialogFragment.bindView(id: Int)
    : ReadOnlyProperty<SupportDialogFragment, V> = required(id, viewFinder)

fun <V : View> Fragment.bindView(id: Int)
    : ReadOnlyProperty<Fragment, V> = required(id, viewFinder)

fun <V : View> SupportFragment.bindView(id: Int)
    : ReadOnlyProperty<SupportFragment, V> = required(id, viewFinder)

fun <V : View> RecyclerView.ViewHolder.bindView(id: Int)
    : ReadOnlyProperty<RecyclerView.ViewHolder, V> = required(id, viewFinder)

fun <V : View> View.bindOptionalView(id: Int)
    : ReadOnlyProperty<View, V?> = optional(id, viewFinder)

fun <V : View> Activity.bindOptionalView(id: Int)
    : ReadOnlyProperty<Activity, V?> = optional(id, viewFinder)

fun <V : View> Dialog.bindOptionalView(id: Int)
    : ReadOnlyProperty<Dialog, V?> = optional(id, viewFinder)

fun <V : View> DialogFragment.bindOptionalView(id: Int)
    : ReadOnlyProperty<DialogFragment, V?> = optional(id, viewFinder)

fun <V : View> SupportDialogFragment.bindOptionalView(id: Int)
    : ReadOnlyProperty<SupportDialogFragment, V?> = optional(id, viewFinder)

fun <V : View> Fragment.bindOptionalView(id: Int)
    : ReadOnlyProperty<Fragment, V?> = optional(id, viewFinder)

fun <V : View> SupportFragment.bindOptionalView(id: Int)
    : ReadOnlyProperty<SupportFragment, V?> = optional(id, viewFinder)

fun <V : View> RecyclerView.ViewHolder.bindOptionalView(id: Int)
    : ReadOnlyProperty<RecyclerView.ViewHolder, V?> = optional(id, viewFinder)

fun <V : View> View.bindViews(vararg ids: Int)
    : ReadOnlyProperty<View, List<V>> = required(ids, viewFinder)

fun <V : View> Activity.bindViews(vararg ids: Int)
    : ReadOnlyProperty<Activity, List<V>> = required(ids, viewFinder)

fun <V : View> Dialog.bindViews(vararg ids: Int)
    : ReadOnlyProperty<Dialog, List<V>> = required(ids, viewFinder)

fun <V : View> DialogFragment.bindViews(vararg ids: Int)
    : ReadOnlyProperty<DialogFragment, List<V>> = required(ids, viewFinder)

fun <V : View> SupportDialogFragment.bindViews(vararg ids: Int)
    : ReadOnlyProperty<SupportDialogFragment, List<V>> = required(ids, viewFinder)

fun <V : View> Fragment.bindViews(vararg ids: Int)
    : ReadOnlyProperty<Fragment, List<V>> = required(ids, viewFinder)

fun <V : View> SupportFragment.bindViews(vararg ids: Int)
    : ReadOnlyProperty<SupportFragment, List<V>> = required(ids, viewFinder)

fun <V : View> ViewHolder.bindViews(vararg ids: Int)
    : ReadOnlyProperty<ViewHolder, List<V>> = required(ids, viewFinder)

fun <V : View> View.bindOptionalViews(vararg ids: Int)
    : ReadOnlyProperty<View, List<V>> = optional(ids, viewFinder)

fun <V : View> Activity.bindOptionalViews(vararg ids: Int)
    : ReadOnlyProperty<Activity, List<V>> = optional(ids, viewFinder)

fun <V : View> Dialog.bindOptionalViews(vararg ids: Int)
    : ReadOnlyProperty<Dialog, List<V>> = optional(ids, viewFinder)

fun <V : View> DialogFragment.bindOptionalViews(vararg ids: Int)
    : ReadOnlyProperty<DialogFragment, List<V>> = optional(ids, viewFinder)

fun <V : View> SupportDialogFragment.bindOptionalViews(vararg ids: Int)
    : ReadOnlyProperty<SupportDialogFragment, List<V>> = optional(ids, viewFinder)

fun <V : View> Fragment.bindOptionalViews(vararg ids: Int)
    : ReadOnlyProperty<Fragment, List<V>> = optional(ids, viewFinder)

fun <V : View> SupportFragment.bindOptionalViews(vararg ids: Int)
    : ReadOnlyProperty<SupportFragment, List<V>> = optional(ids, viewFinder)

fun <V : View> ViewHolder.bindOptionalViews(vararg ids: Int)
    : ReadOnlyProperty<ViewHolder, List<V>> = optional(ids, viewFinder)

private inline val View.viewFinder: View.(Int) -> View?
    get() = { findViewById(it) }
private inline val Activity.viewFinder: Activity.(Int) -> View?
    get() = { findViewById(it) }
private inline val Dialog.viewFinder: Dialog.(Int) -> View?
    get() = { findViewById(it) }
private inline val DialogFragment.viewFinder: DialogFragment.(Int) -> View?
    get() = { dialog.findViewById(it) }
private inline val SupportDialogFragment.viewFinder: SupportDialogFragment.(Int) -> View?
    get() = { dialog.findViewById(it) }
private inline val Fragment.viewFinder: Fragment.(Int) -> View?
    get() = { view.findViewById(it) }
private inline val SupportFragment.viewFinder: SupportFragment.(Int) -> View?
    get() = { view!!.findViewById(it) }
private inline val ViewHolder.viewFinder: ViewHolder.(Int) -> View?
    get() = { itemView.findViewById(it) }

private fun viewNotFound(id: Int, desc: KProperty<*>): Nothing =
    throw IllegalStateException("View ID $id for '${desc.name}' not found.")

private fun <T, V : View> required(id: Int, finder: T.(Int) -> View?) = Lazy { t: T, desc ->
    (t.finder(id) as V?)?.apply { } ?: viewNotFound(id, desc)
}

private fun <T, V : View> optional(id: Int, finder: T.(Int) -> View?) = Lazy { t: T, _ -> t.finder(id) as V? }

private fun <T, V : View> required(ids: IntArray, finder: T.(Int) -> View?) = Lazy { t: T, desc ->
    ids.map {
        t.finder(it) as V? ?: viewNotFound(it, desc)
    }
}

private fun <T, V : View> optional(ids: IntArray, finder: T.(Int) -> View?) =
    Lazy { t: T, _ -> ids.map { t.finder(it) as V? }.filterNotNull() }

// Like Kotlin's lazy delegate but the initializer gets the target and metadata passed to it
private open class Lazy<in T, out V>(private val initializer: (T, KProperty<*>) -> V) : ReadOnlyProperty<T, V> {
    protected object EMPTY

    protected var value: Any? = EMPTY

    override fun getValue(thisRef: T, property: KProperty<*>): V {
        if (value == EMPTY)
            value = initializer(thisRef, property)

        return value as V
    }
}

/*
 * The components below are a variant of the view bindings with lazy resettables
 * All bindings are weakly held so that they may be reset through KotterknifeRegistry.reset
 *
 * This is typically only needed in cases such as Fragments,
 * where their lifecycle doesn't match that of an Activity or View
 *
 * Credits to <a href="https://github.com/MichaelRocks">MichaelRocks</a>
 */

fun <V : View> View.bindViewResettable(id: Int)
    : ReadOnlyProperty<View, V> = requiredResettable(id, viewFinder)

fun <V : View> Activity.bindViewResettable(id: Int)
    : ReadOnlyProperty<Activity, V> = requiredResettable(id, viewFinder)

fun <V : View> Dialog.bindViewResettable(id: Int)
    : ReadOnlyProperty<Dialog, V> = requiredResettable(id, viewFinder)

fun <V : View> DialogFragment.bindViewResettable(id: Int)
    : ReadOnlyProperty<DialogFragment, V> = requiredResettable(id, viewFinder)

fun <V : View> SupportDialogFragment.bindViewResettable(id: Int)
    : ReadOnlyProperty<SupportDialogFragment, V> = requiredResettable(id, viewFinder)

fun <V : View> Fragment.bindViewResettable(id: Int)
    : ReadOnlyProperty<Fragment, V> = requiredResettable(id, viewFinder)

fun <V : View> SupportFragment.bindViewResettable(id: Int)
    : ReadOnlyProperty<SupportFragment, V> = requiredResettable(id, viewFinder)

fun <V : View> ViewHolder.bindViewResettable(id: Int)
    : ReadOnlyProperty<ViewHolder, V> = requiredResettable(id, viewFinder)

fun <V : View> View.bindOptionalViewResettable(id: Int)
    : ReadOnlyProperty<View, V?> = optionalResettable(id, viewFinder)

fun <V : View> Activity.bindOptionalViewResettable(id: Int)
    : ReadOnlyProperty<Activity, V?> = optionalResettable(id, viewFinder)

fun <V : View> Dialog.bindOptionalViewResettable(id: Int)
    : ReadOnlyProperty<Dialog, V?> = optionalResettable(id, viewFinder)

fun <V : View> DialogFragment.bindOptionalViewResettable(id: Int)
    : ReadOnlyProperty<DialogFragment, V?> = optionalResettable(id, viewFinder)

fun <V : View> SupportDialogFragment.bindOptionalViewResettable(id: Int)
    : ReadOnlyProperty<SupportDialogFragment, V?> = optionalResettable(id, viewFinder)

fun <V : View> Fragment.bindOptionalViewResettable(id: Int)
    : ReadOnlyProperty<Fragment, V?> = optionalResettable(id, viewFinder)

fun <V : View> SupportFragment.bindOptionalViewResettable(id: Int)
    : ReadOnlyProperty<SupportFragment, V?> = optionalResettable(id, viewFinder)

fun <V : View> ViewHolder.bindOptionalViewResettable(id: Int)
    : ReadOnlyProperty<ViewHolder, V?> = optionalResettable(id, viewFinder)

fun <V : View> View.bindViewsResettable(vararg ids: Int)
    : ReadOnlyProperty<View, List<V>> = requiredResettable(ids, viewFinder)

fun <V : View> Activity.bindViewsResettable(vararg ids: Int)
    : ReadOnlyProperty<Activity, List<V>> = requiredResettable(ids, viewFinder)

fun <V : View> Dialog.bindViewsResettable(vararg ids: Int)
    : ReadOnlyProperty<Dialog, List<V>> = requiredResettable(ids, viewFinder)

fun <V : View> DialogFragment.bindViewsResettable(vararg ids: Int)
    : ReadOnlyProperty<DialogFragment, List<V>> = requiredResettable(ids, viewFinder)

fun <V : View> SupportDialogFragment.bindViewsResettable(vararg ids: Int)
    : ReadOnlyProperty<SupportDialogFragment, List<V>> = requiredResettable(ids, viewFinder)

fun <V : View> Fragment.bindViewsResettable(vararg ids: Int)
    : ReadOnlyProperty<Fragment, List<V>> = requiredResettable(ids, viewFinder)

fun <V : View> SupportFragment.bindViewsResettable(vararg ids: Int)
    : ReadOnlyProperty<SupportFragment, List<V>> = requiredResettable(ids, viewFinder)

fun <V : View> ViewHolder.bindViewsResettable(vararg ids: Int)
    : ReadOnlyProperty<ViewHolder, List<V>> = requiredResettable(ids, viewFinder)

fun <V : View> View.bindOptionalViewsResettable(vararg ids: Int)
    : ReadOnlyProperty<View, List<V>> = optionalResettable(ids, viewFinder)

fun <V : View> Activity.bindOptionalViewsResettable(vararg ids: Int)
    : ReadOnlyProperty<Activity, List<V>> = optionalResettable(ids, viewFinder)

fun <V : View> Dialog.bindOptionalViewsResettable(vararg ids: Int)
    : ReadOnlyProperty<Dialog, List<V>> = optionalResettable(ids, viewFinder)

fun <V : View> DialogFragment.bindOptionalViewsResettable(vararg ids: Int)
    : ReadOnlyProperty<DialogFragment, List<V>> = optionalResettable(ids, viewFinder)

fun <V : View> SupportDialogFragment.bindOptionalViewsResettable(vararg ids: Int)
    : ReadOnlyProperty<SupportDialogFragment, List<V>> = optionalResettable(ids, viewFinder)

fun <V : View> Fragment.bindOptionalViewsResettable(vararg ids: Int)
    : ReadOnlyProperty<Fragment, List<V>> = optionalResettable(ids, viewFinder)

fun <V : View> SupportFragment.bindOptionalViewsResettable(vararg ids: Int)
    : ReadOnlyProperty<SupportFragment, List<V>> = optionalResettable(ids, viewFinder)

fun <V : View> ViewHolder.bindOptionalViewsResettable(vararg ids: Int)
    : ReadOnlyProperty<RecyclerView.ViewHolder, List<V>> = optionalResettable(ids, viewFinder)

private fun <T, V : View> requiredResettable(id: Int, finder: T.(Int) -> View?) = LazyResettable { t: T, desc ->
    (t.finder(id) as V?)?.apply { } ?: viewNotFound(id, desc)
}

private fun <T, V : View> optionalResettable(id: Int, finder: T.(Int) -> View?) =
    LazyResettable { t: T, _ -> t.finder(id) as V? }

private fun <T, V : View> requiredResettable(ids: IntArray, finder: T.(Int) -> View?) = LazyResettable { t: T, desc ->
    ids.map {
        t.finder(it) as V? ?: viewNotFound(it, desc)
    }
}

private fun <T, V : View> optionalResettable(ids: IntArray, finder: T.(Int) -> View?) =
    LazyResettable { t: T, _ -> ids.map { t.finder(it) as V? }.filterNotNull() }

//Like Kotterknife's lazy delegate but is resettable
private class LazyResettable<in T, out V>(initializer: (T, KProperty<*>) -> V) : Lazy<T, V>(initializer) {
    override fun getValue(thisRef: T, property: KProperty<*>): V {
        KotterknifeRegistry.register(thisRef!!, this)
        return super.getValue(thisRef, property)
    }

    fun reset() {
        value = EMPTY
    }
}

object Kotterknife {
    fun reset(target: Any) {
        KotterknifeRegistry.reset(target)
    }
}

private object KotterknifeRegistry {
    private val lazyMap = WeakHashMap<Any, MutableCollection<LazyResettable<*, *>>>()

    fun register(target: Any, lazy: LazyResettable<*, *>) =
        lazyMap.getOrPut(target, { Collections.newSetFromMap(WeakHashMap()) }).add(lazy)

    fun reset(target: Any) = lazyMap[target]?.forEach(LazyResettable<*, *>::reset)
}