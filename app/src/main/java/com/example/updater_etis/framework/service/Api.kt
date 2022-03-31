package com.example.updater_etis.framework.service


import com.example.updater_etis.BuildConfig
import com.example.updater_etis.app.data.model.Application
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Streaming
import retrofit2.http.Url

interface Api {

    @Headers(
        "Content-Type: application/json",
        "Authorization: ${BuildConfig.TOKEN}"
    )
    @GET("/app")
    suspend fun getApplication() : Application

    @Streaming
    @Headers(
        "Content-Type: application/json",
        "Authorization: ${BuildConfig.TOKEN}"
    )
    @GET
    suspend fun downloadFile(@Url fileUrl:String): Response<ResponseBody>
}