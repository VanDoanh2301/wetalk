package com.example.wetalk.data.model.postmodel

import android.os.Parcelable
import com.example.wetalk.data.model.objectmodel.AnswerRequest
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class QuestionPost(
    @SerializedName("questionId") val questionId: Int,
    @SerializedName("content") val content: String,
    @SerializedName("explanation") val explanation: String?,
    @SerializedName("imageLocation") val imageLocation: String?,
    @SerializedName("videoLocation") val videoLocation: String?,
    @SerializedName("topicId") val topicId: Int,
    @SerializedName("answerReqs") val answers:ArrayList<AnswerRequest>
) : Parcelable {
}