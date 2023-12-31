package com.example.wetalk.data.model.objectmodel

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Question(
    @SerializedName("id") val id: Int,
    @SerializedName("content") val content: String,
    @SerializedName("explanation") val explanation: String,
    @SerializedName("imageLocation") val imageLocation: String,
    @SerializedName("videoLocation") val videoLocation: String,
    @SerializedName("topic_id") val topicId: Int,
    @SerializedName("answerDTOS") val answers:ArrayList<AnswerDTO>
) : Parcelable{
}