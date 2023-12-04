package com.example.wetalk.data.model.responsemodel


import com.google.gson.annotations.SerializedName


data class HostResponse(
    @SerializedName("success") val success: Boolean?,
    @SerializedName("data") val data: Data?,
    @SerializedName("message") val message: String?,
    @SerializedName("code") val code: Int?
)

