package com.example.wetalk.data.model.postmodel

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class TopicPost(
    @SerializedName("content") var content: String,
    @SerializedName("imageLocation") var imageLocation: String,
    @SerializedName("videoLocation") var videoLocation: String
) : Parcelable {
}