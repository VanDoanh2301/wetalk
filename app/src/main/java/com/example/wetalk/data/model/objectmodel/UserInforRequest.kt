package com.example.wetalk.data.model.objectmodel

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class GetAllUserFriendRequest(
    @SerializedName("message") val message: String?,
    @SerializedName("code") val code: Int?,
    @SerializedName("data") val data: ArrayList<UserInforRequest>
) : Parcelable

@Parcelize
data class UserInforRequest(
    @SerializedName("id")
    var id : Int?,
    @SerializedName("name")
    var name: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("phoneNumber")
    var phoneNumber: String,
    @SerializedName("address")
    var address: String,
    @SerializedName("role")
    val role: String,
    @SerializedName("birthDay")
    var age: String,
    @SerializedName("gender")
    var gender: String,
    @SerializedName("avatarLocation")
    val avatarLocation:String
) : Parcelable {}