package com.example.updater_etis.utils

import android.content.Context
import android.util.Log
import java.io.DataOutputStream
import java.io.File
import kotlin.system.exitProcess

var isApkInstalledFailed = false
private var exitInApk = false

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
    exitInApk = true
    context.startActivity(context.packageManager.getLaunchIntentForPackage(Constants.APP_ETIS_PACKAGE_NAME))
}

fun exitInApp() {
    if (exitInApk) {
        exitProcess(0)
    }
}

fun installApp(context: Context, path: String, appName: String) {
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
        isApkInstalledFailed = false
        deleteFileApk(path = path, appName = appName)
        deleteFileApkTemp(path = path, appName = appName)
        openApp(context)
    }.onFailure {
        Log.e(Constants.APP_INSTALL_LOG, "Application install failure. Error: $it")
        isApkInstalledFailed = true
        deleteFileApk(path = path, appName = appName)
        deleteFileApkTemp(path = path, appName = appName)
    }
}

private fun deleteFileApk(path: String, appName: String) {
    val file = File(path, appName)
    if (file.exists()) {
        Log.d(Constants.APP_INSTALL_LOG, "File $appName deleted.")
        file.delete()
    } else {
        Log.d(Constants.APP_INSTALL_LOG, "File $appName - not found.")
    }
}

fun deletePackage() {
    runCatching {
        val command = "pm uninstall -k --user 0 ${Constants.OLD_UPDATER_PACKAGE_NAME}"
        val proc = Runtime.getRuntime().exec(arrayOf("su", "-c", command))
        proc.waitFor()
    }.onSuccess {
        Log.d(Constants.OLD_UPDATER_LOG, "Old updater deleted success.")
    }.onFailure {
        Log.e(Constants.OLD_UPDATER_LOG, "Old updater deleted failure. Error: $it")
    }
}

private fun deleteFileApkTemp(path: String, appName: String) {
    deleteFileApk(path = path, appName = "$appName.temp")
}

fun getAppNameFromUrl(url: String): String {
    return url.substring(url.lastIndexOf("/") + 1)
}