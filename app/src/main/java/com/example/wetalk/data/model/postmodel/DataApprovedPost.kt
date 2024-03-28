package com.example.wetalk.data.model.postmodel

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class DataApprovedPost(@SerializedName("dataCollectionId") var dataCollectionId:Int, @SerializedName("feedBack") var feedBack:String) :Parcelable

@Parcelize
data class DataPost(@SerializedName("dataLocation") var dataCollection:String, @SerializedName("vocabularyId") var vocabularyId:Int) :Parcelable


@Parcelize
data class DataPostSearch(
    @SerializedName("page") val page: Int = 1,
    @SerializedName("size") val size: Int = 50,
    @SerializedName("topic") val topic: String = "",
    @SerializedName("vocabulary") val vocabulary: String,
    @SerializedName("ascending") val ascending: Boolean = true,
    @SerializedName("orderBy") val orderBy: String = "",
    @SerializedName("createdFrom") val createdFrom: String ="",
    @SerializedName("createdTo") val createdTo: String = "",
    @SerializedName("status") val status: Int
) : Parcelable