package com.example.wetalk.data.remote

import com.example.wetalk.data.model.objectmodel.ChatMessage
import com.example.wetalk.data.model.responsemodel.HostResponse

interface SocketManager {
   suspend fun connect()
   suspend fun sendMessage(chatMessage: ChatMessage) : HostResponse
  suspend  fun receiveMessage(callback: (ChatMessage) -> Unit)
}