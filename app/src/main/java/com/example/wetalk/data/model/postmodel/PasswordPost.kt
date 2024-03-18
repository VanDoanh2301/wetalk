package com.example.wetalk.data.model.postmodel

import com.google.gson.annotations.SerializedName

data class PasswordPost(
    @SerializedName("oldPassword")
    val oldPassword: String,
    @SerializedName("newPassword")
    val newPassword: String) {
}