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