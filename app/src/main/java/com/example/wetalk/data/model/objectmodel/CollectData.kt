package com.example.wetalk.data.model.objectmodel

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CollectData(
    @SerializedName("adminEmail") val adminEmail: String?,
    @SerializedName("created") val created: String,
    @SerializedName("dataCollectionId") val dataCollectionId: Int,
    @SerializedName("dataLocation") val dataLocation: String,
    @SerializedName("editor") val editor: String,
    @SerializedName("feedBack") val feedBack: String?,
    @SerializedName("modified") val modified: String,
    @SerializedName("status") val status: Int,
    @SerializedName("vocabularyId") val vocabularyId: Int,
    @SerializedName("volunteerEmail") val volunteerEmail: String
) : Parcelable
@Parcelize
data class GetAllCollectData(
    @SerializedName("message") val message: String?,
    @SerializedName("code") val code: Int?,
    @SerializedName("data") val data: ArrayList<CollectData>

)  : Parcelable