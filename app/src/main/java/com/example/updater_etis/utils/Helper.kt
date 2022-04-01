package com.example.updater_etis.utils

import android.content.Context
import android.util.Log
import java.io.DataOutputStream
import java.io.File

fun isAppInstalled(context: Context, packageName: String): Boolean {
    var isInstalled = false
    runCatching {
        context.packageManager.getApplicationInfo(
            packageName,
            0
        )
    }.onSuccess {
        Log.d(Constants.APP_INSTALL_LOG, "Application install.")
        isInstalled = true
    }.onFailure {
        Log.d(Constants.APP_INSTALL_LOG, "Application not found.")
        isInstalled = false
    }
    return isInstalled
}

fun convertAppVersionToInt(appVersion: String): Int {
    val arr = appVersion.split(".").toTypedArray()
    return (arr[0] + arr[1] + arr[2]).toInt()
}

fun openApp(context: Context) {
    context.startActivity(context.packageManager.getLaunchIntentForPackage(Constants.APP_ETIS_PACKAGE_NAME))
}

fun installApp(path: String, appName: String) {
    runCatching {
        Log.d(Constants.APP_INSTALL_LOG, "Start install application.")
        val command = "pm install -r $path$appName\n"
        val process = Runtime.getRuntime().exec("su")
        val dos = DataOutputStream(process.outputStream)
        dos.apply {
            writeBytes(command)
            writeBytes("exit\n")
            flush()
            close()
        }
        process.waitFor()
    }.onSuccess {
        Log.d(Constants.APP_INSTALL_LOG, "Application install success.")
        val file = File(path, appName)
        if (file.exists()) {
            Log.d(Constants.APP_INSTALL_LOG, "File delete.")
            file.delete()
        } else {
            Log.d(Constants.APP_INSTALL_LOG, "File not found.")
        }
    }.onFailure {
        Log.e(Constants.APP_INSTALL_LOG, "Application install failure. Error: $it")
    }
}