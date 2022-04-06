package com.react___gps

import android.app.Application
import android.content.Context
import com.react___gps.app.presentation.di.dataSourceModules
import com.react___gps.app.presentation.di.repositoryModules
import com.react___gps.app.presentation.di.viewModelModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.logger.Level

class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(this@MainApplication)
            modules(listOf(dataSourceModules, viewModelModules, repositoryModules))
        }
    }

    fun getAppContext(): Context {
        return this@MainApplication
    }
}