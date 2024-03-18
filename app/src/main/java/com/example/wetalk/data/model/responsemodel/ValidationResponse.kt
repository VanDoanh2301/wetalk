package com.example.wetalk.data.model.responsemodel

import com.google.gson.annotations.SerializedName

data class ValidationResponse(
    @SerializedName("content")
    val content: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)