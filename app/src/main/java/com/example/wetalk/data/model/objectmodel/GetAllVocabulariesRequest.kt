package com.example.wetalk.data.model.objectmodel

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetAllVocabulariesRequest(
    @SerializedName("page") val page: Int,
    @SerializedName("totalElements") val totalElements: Int,
    @SerializedName("data") val data: ArrayList<TopicRequest>
) : Parcelable
