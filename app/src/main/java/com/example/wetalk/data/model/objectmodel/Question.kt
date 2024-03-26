package com.example.wetalk.data.model.objectmodel

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Question(
    @SerializedName("questionId") val id: Int,
    @SerializedName("content") val content: String,
    @SerializedName("explanation") val explanation: String,
    @SerializedName("imageLocation") val imageLocation: String,
    @SerializedName("videoLocation") val videoLocation: String,
    @SerializedName("topicId") val topicId: Int,
    @SerializedName("answerResList") val answers:ArrayList<AnswerRequest>
) : Parcelable{
}