package com.example.wetalk.data.model.postmodel

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class MediaValidatePost(
    @SerializedName("videoContent")
    val videoContent: String,
    @SerializedName("videoUrl")
    val videoUrl: String
) : Parcelable