package com.example.wetalk.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wetalk.data.model.objectmodel.ChatMessage
import com.example.wetalk.data.model.responsemodel.HostResponse
import com.example.wetalk.data.remote.SocketManager
import com.example.wetalk.repository.SocketRepository
import com.example.wetalk.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import io.socket.client.Socket
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatHomeViewModel @Inject constructor(private val socketRepository: SocketRepository, private var socket: Socket) : ViewModel() {
    private val _isConnected: MutableLiveData<Resource<Boolean>> = MutableLiveData(Resource.Loading())
    val isConnected: LiveData<Resource<Boolean>> get() = _isConnected

    private val _receivedMessage: MutableLiveData<ChatMessage> = MutableLiveData()
    val receivedMessage: LiveData<ChatMessage> get() = _receivedMessage

    private val _sendMessage: MutableLiveData<Resource<String>> = MutableLiveData(Resource.Loading())
    val sendMessage: LiveData<Resource<String>> get() = _sendMessage

    fun connect() {
        viewModelScope.launch {
            try {
                 socketRepository.connect()
                _isConnected.value = Resource.Success(socket.connected())
            } catch (e: Exception) {
                _isConnected.value = Resource.Error("Connection error: ${e.message}")
            }
        }
    }

    fun sendMessage(chatMessage: ChatMessage) {
        viewModelScope.launch {
            try {
                socketRepository.sendMessage(chatMessage)
                _sendMessage.value = Resource.Success("Sending message success")
            } catch (e: Exception) {
                _sendMessage.value = Resource.Error("Sending message failed: ${e.message}")
            }
        }
    }

    fun receiveMessage() {
        viewModelScope.launch {
            socketRepository.receiveMessage { chatMessage ->
                _receivedMessage.value = chatMessage
            }
        }
    }
}