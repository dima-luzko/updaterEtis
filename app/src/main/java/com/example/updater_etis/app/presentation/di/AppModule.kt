package com.example.updater_etis.app.presentation.di

import com.example.updater_etis.app.presentation.viewModel.CheckInternetConnectionViewModel
import com.example.updater_etis.framework.remote.RemoteDataSource
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val dataSourceModules = module {
    single { RemoteDataSource }
}

val viewModelModules = module {
    viewModel { CheckInternetConnectionViewModel(get()) }
}
