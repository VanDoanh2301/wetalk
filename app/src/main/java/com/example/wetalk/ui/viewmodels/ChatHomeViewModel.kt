package com.example.wetalk.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.liveData
import com.example.wetalk.data.model.objectmodel.ChatMessage
import com.example.wetalk.data.model.objectmodel.GetAllUserFriendRequest
import com.example.wetalk.data.model.objectmodel.Message
import com.example.wetalk.data.model.objectmodel.MessagePaging
import com.example.wetalk.paging.MessagePagingSource
import com.example.wetalk.repository.TalkRepository
import com.example.wetalk.util.BASE_URL_SOCKET
import com.example.wetalk.util.Resource
import com.example.wetalk.util.SharedPreferencesUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import io.socket.client.Ack
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import java.net.URISyntaxException
import javax.inject.Inject

enum class ChatStatus { LOADING, ERROR, CONNECT, DONE }

@HiltViewModel
class ChatHomeViewModel @Inject constructor(private val repository: TalkRepository) : ViewModel() {
    private lateinit var socket: Socket
    private val _chatMessages = MutableLiveData<ChatMessage>()
    val chatMessages: LiveData<ChatMessage> get() = _chatMessages
    private val _chatStatus = MutableLiveData<ChatStatus>()
    val chatStatus: LiveData<ChatStatus> get() = _chatStatus
    private var _messages = MutableLiveData<Resource<List<Message>>>(Resource.Loading())
    val messages: LiveData<Resource<List<Message>>> get() = _messages
    private var messageResponse: List<Message>? = null


    fun loadMore(messagePaging: MessagePaging): LiveData<PagingData<Message>> {
        val pagingData = repository.getLimitMessages(messagePaging).cachedIn(viewModelScope)
        return pagingData
    }


    init {
        val options = IO.Options().apply {
            query =
                "conversationId=${SharedPreferencesUtils.getRoom()}" + "&contactId=${SharedPreferencesUtils.getCurrentUser()}"
        }
        try {
            socket = IO.socket(BASE_URL_SOCKET, options)

        } catch (e: URISyntaxException) {
            e.printStackTrace()
            _chatStatus.value = ChatStatus.ERROR
        }

    }
    fun connectSocket() {
        socket.on(Socket.EVENT_CONNECT, onConnect)
        socket.on("get_message", onMessageReceived)
        socket.connect()
    }

    private val onConnect = Emitter.Listener {
        Log.d("SocketIO", "Connected")
        _chatStatus.postValue(ChatStatus.CONNECT)
    }
    private val onMessageReceived = Emitter.Listener { args ->
        try {
            val jsonString = args[0] as JSONObject
            val content = jsonString.getString("content")
            val contactId = jsonString.getString("contactId")
            val messageType = jsonString.getString("messageType")
            val chatMessage = ChatMessage(contactId.toInt(), content, messageType, null)
            _chatMessages.postValue(chatMessage)
            _chatStatus.postValue(ChatStatus.DONE)
        } catch (c: ClassCastException) {
            val jsonString = args[0] as String
            _chatStatus.postValue(ChatStatus.ERROR)
        }
    }


    fun sendMessageClient(chatMessage: ChatMessage) {
        _chatStatus.postValue(ChatStatus.LOADING)
        val jsonObject = JSONObject()
        jsonObject.put("contactId", chatMessage.id)
        jsonObject.put("content", chatMessage.content)
        jsonObject.put("messageType", chatMessage.messageType)
        jsonObject.put("mediaLocation", chatMessage.mediaLocation)
        socket.emit("send_message", jsonObject, Ack { args ->
            if (args.isNotEmpty()) {
                _chatStatus.postValue(ChatStatus.ERROR)
            } else {
                _chatStatus.postValue(ChatStatus.DONE)
            }
        })

    }

    fun getAllMessage(conversationId: Int) = viewModelScope.launch {
        try {
            val response = repository.getAllMessage(conversationId)
            _messages.value = handleGetAllMessages(response)
        } catch (e: Exception) {
            _messages.value = Resource.Error(e.message.toString())
        }
    }

    private fun handleGetAllMessages(response: Response<List<Message>>): Resource<List<Message>>? {
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Success(messageResponse ?: it)
            }
        } else {
        }
        return Resource.Error((messageResponse ?: response.message()).toString())
    }

    fun onDestroy() {
        socket.disconnect()
    }
}