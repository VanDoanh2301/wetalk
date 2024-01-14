package com.example.wetalk.data.local

import com.google.firebase.Timestamp

data class ChatroomModel(
    val chatroomId: String,
    val userIds: List<String>,
    val lastMessageTimestamp: Timestamp,
    val lastMessageSenderId: String,
    val lastMessage: String
)