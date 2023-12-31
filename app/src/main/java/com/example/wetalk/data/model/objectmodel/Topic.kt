package com.example.wetalk.data.model.objectmodel

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Topic(
    @SerializedName("id") var id: Int,
    @SerializedName("content") var content: String,
    @SerializedName("imageLocation") var imageLocation: String,
    @SerializedName("videoLocation") var videoLocation: String
) : Parcelable {
}