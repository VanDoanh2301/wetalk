package com.example.wetalk.data.model.responsemodel

import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("access_token") val accessToken: String?,
    @SerializedName("refresh_token") val refreshToken: String?,
)
