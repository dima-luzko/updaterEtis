package com.example.updater_etis.utils

import com.example.updater_etis.MainApplication

class Constants {
    companion object {
        const val BASE_URL = "test.release.horizont-rnd.by"
        const val APP_NAME = "E.T.I.S."
        const val APP_ETIS_PACKAGE_NAME = "com.horizont.etis"
        const val OLD_UPDATER_PACKAGE_NAME = "com.example.kkaminets.updateretis"
        const val INTERNET_CONNECTED_LOG = "INTERNET_CONNECTED"
        const val OLD_UPDATER_LOG = "OLD_UPDATER"
        const val APP_INSTALL_LOG = "APP_INSTALL"
        const val PERMISSION_LOG = "PERMISSION"
        const val DOWNLOAD_LOG = "DOWNLOAD"
        const val REQUEST_PERMISSION_CODE = 102
        const val DOWNLOAD_TIMEOUT_CONNECTION = 1 * 60 * 1000
    }
}