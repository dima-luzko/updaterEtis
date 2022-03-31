package com.example.updater_etis.app.domain.repository

import okhttp3.ResponseBody
import retrofit2.Response
import com.example.updater_etis.app.data.model.Application


interface ApplicationRepository {
    suspend fun getApplicationInfo(): Application
}