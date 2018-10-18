package ca.allanwang.kau.kotlin

/**
 * Created by Allan Wang on 07/04/18.
 */
inline fun <reified T : Any> javaClass(): Class<T> = T::class.java