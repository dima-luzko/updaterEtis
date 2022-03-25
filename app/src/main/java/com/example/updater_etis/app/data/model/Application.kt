package com.example.updater_etis.app.data.model

import com.google.gson.annotations.SerializedName

data class Application(
    @SerializedName("name")
    val name: String,
    @SerializedName("version")
    val version : String,
    @SerializedName("app")
    val appUrl: String
)
