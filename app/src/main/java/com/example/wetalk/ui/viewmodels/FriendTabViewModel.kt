package com.example.wetalk.ui.viewmodels

import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wetalk.data.model.objectmodel.GetAllUserFriendRequest
import com.example.wetalk.data.model.responsemodel.HostResponse
import com.example.wetalk.repository.TalkRepository
import com.example.wetalk.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class FriendTabViewModel @Inject constructor(private var repository: TalkRepository) : ViewModel() {
    private var _pendingFriend: MutableLiveData<Resource<GetAllUserFriendRequest>> =
        MutableLiveData(Resource.Loading())
    val pendingFriend: LiveData<Resource<GetAllUserFriendRequest>> get() = _pendingFriend
    private var pendingFriendResponse: GetAllUserFriendRequest? = null

    private var _friends: MutableLiveData<Resource<GetAllUserFriendRequest>> =
        MutableLiveData(Resource.Loading())
    val friends: LiveData<Resource<GetAllUserFriendRequest>> get() = _friends
    private var friendsResponse: GetAllUserFriendRequest? = null

    private var _acceptFriend: MutableStateFlow<Resource<HostResponse>> =
        MutableStateFlow(Resource.Loading())
    val acceptFriend: StateFlow<Resource<HostResponse>> get() = _acceptFriend
    private var acceptResponse: HostResponse? = null

    fun getAllFriendPending() = viewModelScope.launch {
        try {
            val response = repository.pendingFriend()
            _pendingFriend.value = handleGetALlFriendPending(response)
        } catch (e: Exception) {
            _pendingFriend.value = Resource.Error(e.message.toString())
        }
    }

    private fun handleGetALlFriendPending(response: Response<GetAllUserFriendRequest>): Resource<GetAllUserFriendRequest>? {
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Success(pendingFriendResponse ?: it)
            }
        } else {
        }
        return Resource.Error((pendingFriendResponse ?: response.message()).toString())
    }

    fun getAllFriend() = viewModelScope.launch {
        try {
            val response = repository.getAllFriend()
            _friends.value = handleGetAllFriend(response)
        } catch (e: Exception) {
            _friends.value = Resource.Error(e.message.toString())
        }
    }

    private fun handleGetAllFriend(response: Response<GetAllUserFriendRequest>): Resource<GetAllUserFriendRequest>? {
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Success(friendsResponse ?: it)
            }
        } else {
        }
        return Resource.Error((friendsResponse ?: response.message()).toString())
    }

    fun postAcceptFriend(userId: Int) = viewModelScope.launch {
        try {
            val response = repository.acceptFriend(userId)
            _acceptFriend.value = handleAcceptFriend(response)
        } catch (e: Exception) {
            _acceptFriend.value = Resource.Error(e.message.toString())
        }
    }

    private fun handleAcceptFriend(response: Response<HostResponse>): Resource<HostResponse> {
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Success(acceptResponse ?: it)
            }
        } else {
        }
        return Resource.Error((acceptResponse ?: response.message()).toString())
    }


}