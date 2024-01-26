package com.example.wetalk.data.model.postmodel

import com.google.gson.annotations.SerializedName

data class UserOtpDTO(
    @SerializedName("email")
    val email: String,
    @SerializedName("otpNum")
    val otpNum:Int
) {
}