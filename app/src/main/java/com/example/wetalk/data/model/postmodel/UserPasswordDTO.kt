package com.example.wetalk.data.model.postmodel

import com.google.gson.annotations.SerializedName

data class UserPasswordDTO(
    @SerializedName("oldPassword")
    val oldPassword: String,
    @SerializedName("newPassword")
    val newPassword: String) {
}