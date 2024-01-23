package com.example.wetalk.ui.viewmodels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wetalk.data.model.objectmodel.GetAllUserInforRequest
import com.example.wetalk.data.model.objectmodel.UserQueryRequest
import com.example.wetalk.data.model.responsemodel.HostResponse
import com.example.wetalk.repository.TalkRepository
import com.example.wetalk.util.NetworkUtil
import com.example.wetalk.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class TalkSearchUserViewModel @Inject constructor(
    private val repository: TalkRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    // MutableStateFlow to represent the state of the user information retrieval
    private val _getInforUser = MutableStateFlow<Resource<GetAllUserInforRequest>>(Resource.Loading())
    // Publicly exposed StateFlow to observe the state changes of user information retrieval
    val getInforUser: StateFlow<Resource<GetAllUserInforRequest>> get() = _getInforUser
    // MutableStateFlow to represent the state of adding a friend operation
    private val _getAddFriend = MutableStateFlow<Resource<HostResponse>>(Resource.Loading())
    // Publicly exposed StateFlow to observe the state changes of adding a friend operation
    val getAddFriend: StateFlow<Resource<HostResponse>> get() = _getAddFriend
    // Variable to store user information (if needed)
    private var userInfor: GetAllUserInforRequest? = null
    // Variable to store the response from adding a friend operation (if needed)
    private var hostResponse: HostResponse? = null


    /**
     * Initiates a user search based on the provided [userQueryRequest].
     * Updates the [_getInforUser] StateFlow with the result of the search operation.
     */
    fun searchUser(userQueryRequest: UserQueryRequest) = viewModelScope.launch {
        try {
            // Check for internet connection before making the API call
            _getInforUser.value = if (NetworkUtil.hasInternetConnection(context)) {
                // If there is an internet connection, handle the API response
                handleSearchUserResponse(repository.seacherUser(userQueryRequest))
            } else {
                // If there is no internet connection, update the StateFlow with an error
                Resource.Error("Mất Kết Nối Internet")
            }
        } catch (e: Exception) {
            // Handle exceptions and update the StateFlow with an error
            _getInforUser.value = Resource.Error(e.toString())
        }
    }

    /**
     * Handles the response from the user search API call.
     * If the response is successful, returns a [Resource.Success] with the user information.
     * If the response is not successful, returns a [Resource.Error] with an error message.
     */
    private fun handleSearchUserResponse(response: Response<GetAllUserInforRequest>): Resource<GetAllUserInforRequest> =
        if (response.isSuccessful) {
            // If the API call is successful, return a Success resource with the user information
            Resource.Success(userInfor ?: response.body()!!)
        } else {
            // If the API call is not successful, return an Error resource with the error message
            Resource.Error((userInfor ?: response.message()).toString())
        }


    fun addFriend(userId: Int) = viewModelScope.launch {
        try {
            _getAddFriend.value = if (NetworkUtil.hasInternetConnection(context)) {
                handleAddFriendResponse(repository.addFriend(userId))
            } else {
                Resource.Error("Mất Kết Nối Internet")
            }
        } catch (e: Exception) {
            _getAddFriend.value = Resource.Error(e.toString())
        }
    }

    private fun handleAddFriendResponse(response: Response<HostResponse>): Resource<HostResponse> =
        if (response.isSuccessful) {
            Toast.makeText(context, "Đã gửi lời mời kết bạn", Toast.LENGTH_LONG).show()
            Resource.Success(hostResponse ?: response.body()!!)

        } else {
            Resource.Error((hostResponse ?: response.message()).toString())
        }
}
