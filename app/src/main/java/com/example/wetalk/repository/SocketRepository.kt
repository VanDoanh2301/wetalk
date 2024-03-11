package com.example.wetalk.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.wetalk.data.model.objectmodel.ChatMessage
import com.example.wetalk.data.model.responsemodel.HostResponse
import com.example.wetalk.data.remote.SocketManager
import com.example.wetalk.util.SharedPreferencesUtils
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SocketRepository @Inject constructor(private var socket: Socket) : SocketManager {
    override suspend fun connect()  {
        if (!socket.connected()) {
            socket.on(
                Socket.EVENT_CONNECT
            ) {
                Log.d("SocketIO", "Connected")
            }
            socket.connect()
        }
    }

    override suspend fun sendMessage(chatMessage: ChatMessage) : HostResponse {
        var messageResponse = ""
        if (socket.connected()) {
            val jsonObject = JSONObject()
            jsonObject.put("id", chatMessage.id)
            jsonObject.put("content", chatMessage.content)
            jsonObject.put("messageType", chatMessage.messageType)
            jsonObject.put("mediaLocation", chatMessage.mediaLocation)
            socket.emit("send_message", jsonObject)
            socket.on("send_message") {args ->
                val chatMessage = args[0] as ChatMessage
                if (chatMessage != null) {
                    messageResponse = "Send success"
                }
            }

        } else {
            messageResponse = "Don't connect"
        }
        var hostResponse = HostResponse(messageResponse, 200)
        return hostResponse
    }

    override suspend fun receiveMessage(callback: (ChatMessage) -> Unit) {
        socket.on("get_message", Emitter.Listener { args ->
            val chatMessage = args[0] as ChatMessage
            callback(chatMessage)
        })
    }

}

