package com.example.wetalk.data.model.objectmodel

import android.os.Parcelable
import com.example.wetalk.data.model.postmodel.UpdateUserPost
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetAllUserRequest(
    @SerializedName("message") val message: String?,
    @SerializedName("code") val code: Int?,
    @SerializedName("data") val data: ArrayList<UpdateUserPost>
) : Parcelable