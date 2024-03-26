package com.example.wetalk.data.model.objectmodel

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class VocabularyRequest(
    @SerializedName("vocabularyId") var id: Int,
    @SerializedName("content") var content: String,
    @SerializedName("imageLocation") var imageLocation: String?,
    @SerializedName("videoLocation") var videoLocation: String?,
    @SerializedName("topicId") var topicId: Int,
) : Parcelable {
}