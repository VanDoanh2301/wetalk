package com.example.wetalk.data.model.responsemodel


import com.google.gson.annotations.SerializedName


data class HostResponse(
    @SerializedName("message") val message: String?,
    @SerializedName("code") val code: Int?
)

