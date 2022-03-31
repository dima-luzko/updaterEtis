package com.example.updater_etis.utils

import android.content.Context
import android.util.Log
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.downloader.PRDownloaderConfig
import com.example.updater_etis.app.presentation.viewModel.CheckInternetConnectionViewModel


object DownloadHelper {

    fun downloadApk(
        context: Context,
        url: String,
        dirPath: String,
        fileName: String,
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
                Log.d("DOWNLOADSS", "START DOWNLOAD")
            }.setOnProgressListener {
                Log.d("DOWNLOADSS", "progress - ${it.currentBytes * 100 / it.totalBytes}")
            }.start(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    Log.d("DOWNLOADSS", "FINISH")
                }

                override fun onError(error: Error?) {
                    with(viewModel) {
                        startNetworkCheckState()
                        startCheckExitValue()
                    }
                    Log.d("DOWNLOADSS", "ERROR - $error")
                }
            })
    }

    fun getAppNameFromUrl(url: String): String {
        return url.substring(url.lastIndexOf("/") + 1)
    }

}