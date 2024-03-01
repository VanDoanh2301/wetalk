package com.example.wetalk.data.model.objectmodel

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserUpdateRequest(
    @SerializedName("name")
    var name: String,
    @SerializedName("phoneNumber")
    var phoneNumber: String,
    @SerializedName("address")
    var address: String,
    @SerializedName("birthDay")
    var birthDay: String,
    @SerializedName("gender")
    var gender: String

):Parcelable {
}