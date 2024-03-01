package com.example.wetalk.data.model.objectmodel

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class AnswerRequest(
    @SerializedName("content") val content: String,
    @SerializedName("imageLocation") val imageLocation: String,
    @SerializedName("videoLocation") val videoLocation: String,
    @SerializedName("correct") val correct: Boolean
) : Parcelable