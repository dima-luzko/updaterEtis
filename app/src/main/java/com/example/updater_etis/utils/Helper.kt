package com.example.updater_etis.utils

import android.content.Context

fun isAppInstalled(context: Context, packageName: String): Boolean {
    var isInstalled = false
    runCatching {
        context.packageManager.getApplicationInfo(
            packageName,
            0
        )
    }.onSuccess {
        isInstalled = true
    }.onFailure {
        isInstalled = false
    }
    return isInstalled
}

fun convertAppVersionToInt(appVersion: String): Int {
    val arr = appVersion.split(".").toTypedArray()
    return (arr[0] + arr[1] + arr[2]).toInt()
}