package com.example.wetalk.data.model.objectmodel

import com.google.gson.annotations.SerializedName

data class ChatMessage(
    var id: Int,
    var content: String,
    var messageType: String = "TEXT",
    var mediaLocation: String?
)
data class Message(
    @SerializedName("messageId")
    val messageId: Int,
    @SerializedName("content")
    val content: String,
    @SerializedName("messageType")
    val messageType: String,
    @SerializedName("mediaLocation")
    val mediaLocation: String?,
    @SerializedName("status")
    val status: Int?,
    @SerializedName("created")
    val created: String?,
    @SerializedName("conversationId")
    val conversationId: Int,
    @SerializedName("contactId")
    val contactId: Int
)