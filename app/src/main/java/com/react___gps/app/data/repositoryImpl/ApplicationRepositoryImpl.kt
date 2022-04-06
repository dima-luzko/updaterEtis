package com.react___gps.app.data.repositoryImpl

import com.react___gps.app.data.model.Application
import com.react___gps.app.domain.repository.ApplicationRepository
import com.react___gps.framework.remote.RemoteDataSource

class ApplicationRepositoryImpl(private val dataSource: RemoteDataSource) : ApplicationRepository {

    override suspend fun getApplicationInfo(): Application {
        return dataSource.retrofit.getApplication()
    }

}