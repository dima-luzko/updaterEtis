package com.react___gps.app.presentation.di

import com.react___gps.app.data.repositoryImpl.ApplicationRepositoryImpl
import com.react___gps.app.domain.repository.ApplicationRepository
import com.react___gps.app.presentation.viewModel.CheckInternetConnectionViewModel
import com.react___gps.framework.remote.RemoteDataSource
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val dataSourceModules = module {
    single { RemoteDataSource }
}

val viewModelModules = module {
    viewModel { CheckInternetConnectionViewModel() }
}

val repositoryModules = module {
    single<ApplicationRepository> { ApplicationRepositoryImpl(get()) }
}
