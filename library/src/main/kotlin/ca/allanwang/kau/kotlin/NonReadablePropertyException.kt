package ca.allanwang.kau.kotlin

/**
 * Created by Allan Wang on 2017-06-24.
 *
 * Credits to @zsmb13
 *
 * https://github.com/zsmb13/MaterialDrawerKt/blob/master/library/src/main/java/co/zsmb/materialdrawerkt/NonReadablePropertyException.kt
 */
class NonReadablePropertyException : Exception()

fun nonReadable(): Nothing = throw NonReadablePropertyException()