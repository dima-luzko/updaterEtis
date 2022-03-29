package com.example.updater_etis.app.data.repositoryImpl

import com.example.updater_etis.app.data.model.Application
import com.example.updater_etis.app.domain.repository.ApplicationRepository
import com.example.updater_etis.framework.remote.RemoteDataSource

class ApplicationRepositoryImpl(private val dataSource: RemoteDataSource): ApplicationRepository {

    override suspend fun getApplicationInfo(): Application {
        return dataSource.retrofit.getApplication()
    }
}