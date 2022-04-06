package com.react___gps.app.domain.repository

import com.react___gps.app.data.model.Application


interface ApplicationRepository {
    suspend fun getApplicationInfo(): Application
}