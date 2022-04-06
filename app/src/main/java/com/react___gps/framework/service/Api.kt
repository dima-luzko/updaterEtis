package com.react___gps.framework.service



import com.react___gps.BuildConfig
import com.react___gps.app.data.model.Application
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