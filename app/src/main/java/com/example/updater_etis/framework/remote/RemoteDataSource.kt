package com.example.updater_etis.framework.remote

import com.example.updater_etis.framework.service.Api
import com.example.updater_etis.utils.Constants
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RemoteDataSource {
    private val gson = GsonBuilder().create()

    val retrofit: Api = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(getHttpClient())
        .build()
        .create(Api::class.java)

    private fun getHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val builder = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(5L, TimeUnit.SECONDS)
        return builder.build()
    }
}