package com.react___gps.app.data.model

import com.google.gson.annotations.SerializedName

data class Application(
    @SerializedName("name_app")
    val name: String,
    @SerializedName("version")
    val version : String,
    @SerializedName("app")
    val appUrl: String
)
