package com.example.wetalk.data.model.objectmodel

import com.google.gson.annotations.SerializedName

data class AvatarRequest(
    @SerializedName("avatarLocation")
    val avatarLocation: String
)