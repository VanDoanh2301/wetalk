package com.example.wetalk.data.model.objectmodel

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    @SerializedName("name")
    val name: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("phoneNumber")
    val phoneNumber: String,

    @SerializedName("address")
    val address: String,

    @SerializedName("role")
    val role: String,

    @SerializedName("age")
    val age: Int,

    @SerializedName("gender")
    val gender: String
) : Parcelable{}