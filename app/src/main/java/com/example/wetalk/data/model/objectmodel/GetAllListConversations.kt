package com.example.wetalk.data.model.objectmodel

import com.google.gson.annotations.SerializedName

data class GetAllListConversations(
    @SerializedName("conversationId") val conversationId: Int,
    @SerializedName("grouAttachConvResList") val grouAttachConvResList: List<GroupAttachmentConversation>
)

data class LastMessage(
    @SerializedName("content") val content: String,
    @SerializedName("messageType") val messageType: String,
    @SerializedName("mediaLocation") val mediaLocation: String,
    @SerializedName("status") val status: Int,
    @SerializedName("created") val created: String,
    @SerializedName("contactName") val contactName: String
)

data class GroupAttachmentConversation(
    @SerializedName("contactName") val contactName: String,
    @SerializedName("avatarLocation") val avatarLocation: String,
    @SerializedName("email") val email: String,
    @SerializedName("lastActivity") val lastActivity: String,
    @SerializedName("lastMessageRes") val lastMessageRes: LastMessage
)

