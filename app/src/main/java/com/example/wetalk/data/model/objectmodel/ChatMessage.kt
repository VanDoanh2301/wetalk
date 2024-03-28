package com.example.wetalk.data.model.objectmodel

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetAllChatMessage(
    @SerializedName("message") val message: String?,
    @SerializedName("code") val code: Int?,
    @SerializedName("data") val data: ArrayList<Message>
) : Parcelable
@Parcelize
data class ChatMessage(
    var id: Int,
    var content: String,
    var messageType: String = "TEXT",
    var mediaLocation: String?
) : Parcelable

@Parcelize
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
): Parcelable

data class MessagePaging(
    @SerializedName("page")
    var page: Int,
    @SerializedName("size")
    var size: Int,
    @SerializedName("conversationId")
var conversationId: Int
)