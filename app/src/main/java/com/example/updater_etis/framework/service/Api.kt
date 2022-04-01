package com.example.updater_etis.framework.service


import com.example.updater_etis.BuildConfig
import com.example.updater_etis.app.data.model.Application
import retrofit2.http.GET
import retrofit2.http.Headers

interface Api {

    @Headers(
        "Content-Type: application/json",
        "Authorization: ${BuildConfig.TOKEN}"
    )
    @GET("/app")
    suspend fun getApplication(): Application
}