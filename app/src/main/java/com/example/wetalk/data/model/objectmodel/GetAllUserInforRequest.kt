package com.example.wetalk.data.model.objectmodel

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetAllUserInforRequest(
    @SerializedName("page") val page: Int,
    @SerializedName("totalElements") val totalElements: Int,
    @SerializedName("data") val data: ArrayList<UserInforRequest>
) : Parcelable
