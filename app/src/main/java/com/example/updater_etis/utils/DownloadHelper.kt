package com.example.updater_etis.utils

import android.content.Context
import android.util.Log
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.downloader.PRDownloaderConfig
import com.example.updater_etis.app.presentation.viewModel.CheckInternetConnectionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


object DownloadHelper {

    fun downloadApk(
        context: Context,
        url: String,
        dirPath: String,
        fileName: String,
        appName: String,
        viewModel: CheckInternetConnectionViewModel
    ) {
        PRDownloader.initialize(context)
        val config = PRDownloaderConfig.newBuilder()
            .setReadTimeout(Constants.DOWNLOAD_TIMEOUT_CONNECTION)
            .setConnectTimeout(Constants.DOWNLOAD_TIMEOUT_CONNECTION)
            .build()
        PRDownloader.initialize(context, config)
        PRDownloader.download(url, dirPath, fileName).build()
            .setOnStartOrResumeListener {
                Log.d(Constants.DOWNLOAD_LOG, "Start download.")
            }.setOnProgressListener {
                Log.d(
                    Constants.DOWNLOAD_LOG,
                    "Download progress - ${it.currentBytes * 100 / it.totalBytes}%"
                )
            }.start(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    Log.d(Constants.DOWNLOAD_LOG, "Download completed.")
                    CoroutineScope(Dispatchers.IO).launch {
                        //TODO: что делать если мы словили ошибку при установке (так как нет рут прав)
                        installApp(context = context, path = dirPath, appName = appName)
                    }
                }

                override fun onError(error: Error?) {
                    with(viewModel) {
                        startNetworkCheckState()
                        startCheckExitValue()
                    }
                    Log.e(Constants.DOWNLOAD_LOG, "Download error: $error")
                }
            })
    }
}