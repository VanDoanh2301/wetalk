package com.example.wetalk.data.model.objectmodel

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class QuestionSize(@SerializedName("page") var page:Int, @SerializedName("size") var size:Int, @SerializedName("topicId") var topicId:Int) :Parcelable
