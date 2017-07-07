package ca.allanwang.kau.utils

import android.content.Context
import android.net.ConnectivityManager

/**
 * Created by Allan Wang on 2017-07-07.
 */
inline val Context.isNetworkAvailable: Boolean
    get() {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo?.isConnectedOrConnecting ?: false
    }

inline val Context.isWifiConnected: Boolean
    get() {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return (activeNetworkInfo?.type ?: -1) == ConnectivityManager.TYPE_WIFI
    }

inline val Context.isMobileDataConnected: Boolean
    get() {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return (activeNetworkInfo?.type ?: -1) == ConnectivityManager.TYPE_MOBILE
    }