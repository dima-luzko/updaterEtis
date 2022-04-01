package com.example.updater_etis.app.domain.repository

import com.example.updater_etis.app.data.model.Application


interface ApplicationRepository {
    suspend fun getApplicationInfo(): Application
}