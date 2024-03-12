package com.example.wetalk.data.model.objectmodel

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetAllListConversations(
    @SerializedName("conversationId") val conversationId: Int,
    @SerializedName("grouAttachConvResList") val grouAttachConvResList: List<GroupAttachmentConversation>
) : Parcelable

@Parcelize
data class LastMessage(
    @SerializedName("content") val content: String?,
    @SerializedName("messageType") val messageType: String?,
    @SerializedName("mediaLocation") val mediaLocation: String?,
    @SerializedName("status") val status: Int?,
    @SerializedName("created") val created: String?,
    @SerializedName("contactName") val contactName: String?
): Parcelable
@Parcelize
data class GroupAttachmentConversation(
    @SerializedName("contactName") val contactName: String,
    @SerializedName("avatarLocation") val avatarLocation: String? =null,
    @SerializedName("email") val email: String,
    @SerializedName("lastActivity") val lastActivity: String? =null,
    @SerializedName("lastMessageRes") val lastMessageRes: LastMessage? =null
): Parcelable

