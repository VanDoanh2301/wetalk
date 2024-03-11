package com.example.wetalk.data.model.objectmodel

import com.google.gson.annotations.SerializedName

data class RoomConversation(
    @SerializedName("conversationName") var conversationName: String,
    @SerializedName("conversationType") var conversationType: String,
    @SerializedName("contactIds") var contactIds: ArrayList<Int>
)

enum class ConversationType {
    SINGLE,
    GROUP
}



