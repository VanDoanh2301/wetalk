package com.example.wetalk.ui.viewmodels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wetalk.data.model.objectmodel.AvatarRequest
import com.example.wetalk.data.model.objectmodel.GetAllUserRequest
import com.example.wetalk.data.model.objectmodel.UserInforRequest
import com.example.wetalk.data.model.postmodel.PasswordPost
import com.example.wetalk.data.model.postmodel.UpdateUserPost
import com.example.wetalk.repository.TalkRepository
import com.example.wetalk.util.LogUtils
import com.example.wetalk.util.NetworkUtil
import com.example.wetalk.util.NetworkUtil.Companion.hasInternetConnection
import com.example.wetalk.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ProfileHomeViewModel @Inject constructor(
    private val repository: TalkRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    // StateFlow for getting user information
    private val _getInforUser: MutableStateFlow<Resource<UserInforRequest>> =
        MutableStateFlow(Resource.Loading())
    val getInforUser: StateFlow<Resource<UserInforRequest>> get() = _getInforUser
    // StateFlow for updating user information
    private val _updateUser: MutableStateFlow<Resource<GetAllUserRequest>> =
        MutableStateFlow(Resource.Loading())
    val updateUser: StateFlow<Resource<GetAllUserRequest>> get() = _updateUser
    // StateFlow for updating user avatar
    private val _updateAvatar: MutableStateFlow<Resource<GetAllUserRequest>> =
        MutableStateFlow(Resource.Loading())
    val updateAvatar: StateFlow<Resource<GetAllUserRequest>> get() = _updateAvatar
    // LiveData for uploading video result
    private val _uploadResult: MutableLiveData<Resource<String>> =
        MutableLiveData(Resource.Loading())
    val uploadResult: LiveData<Resource<String>> get() = _uploadResult

    private var user: UserInforRequest? = null
    private var userInfor: GetAllUserRequest? = null
    private var res: String? = null

    // Function to get user information
    fun getUser() = viewModelScope.launch() {
        safeGetAllUsers()
    }

    // Function to safely update user avatar
    fun safeUpdateAvatarUser(avatarRequest: AvatarRequest) =
        viewModelScope.launch {
            safeUserAvatar(avatarRequest)
        }

    // Function to handle updating user avatar
    private suspend fun safeUserAvatar(avatarRequest: AvatarRequest) {
        try {
            if (hasInternetConnection(context)) {
                val response = repository.updateAvata(avatarRequest)
                _updateAvatar.value = handleUpdateAvatarResponse(response)
            } else {
                _updateAvatar.value = Resource.Error("Lost Internet Connection")
            }
        } catch (e: Exception) {
            Log.e("GETALLUSERS_API_ERROR", e.toString())
            _getInforUser.value = Resource.Error(e.toString())
        }
    }

    // Function to handle response for updating user avatar
    private fun handleUpdateAvatarResponse(response: Response<GetAllUserRequest>): Resource<GetAllUserRequest> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(userInfor ?: resultResponse)
            }
        } else {
            Log.e("GETALLUSERS_RETROFIT_ERROR", response.toString())
        }
        return Resource.Error((userInfor ?: response.message()).toString())
    }

    // Function to update user information
    fun updateInforUser(userRequest: UpdateUserPost) = viewModelScope.launch {
        safeUpdateUser(userRequest)
    }

    // Function to safely update user information
    private suspend fun safeUpdateUser(userRequest: UpdateUserPost) {
        try {
            if (hasInternetConnection(context)) {
                val response = repository.updateUser(userRequest)
                _updateUser.value = handleUpdateUsersResponse(response)
            } else {
                _updateUser.value = Resource.Error("Lost Internet Connection")
            }
        } catch (e: Exception) {
            Log.e("GETALLUSERS_API_ERROR", e.toString())
            _getInforUser.value = Resource.Error(e.toString())
        }
    }

    // Function to handle response for updating user information
    private fun handleUpdateUsersResponse(response: Response<GetAllUserRequest>): Resource<GetAllUserRequest> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(userInfor ?: resultResponse)
            }
        } else {
            Log.e("GETALLUSERS_RETROFIT_ERROR", response.toString())
        }
        return Resource.Error((user ?: response.message()).toString())
    }

    // Function to safely get all user information
    private suspend fun safeGetAllUsers() {
        try {
            if (hasInternetConnection(context)) {
                val response = repository.getUserInfor()
                _getInforUser.value = handleGetAllUsersResponse(response)
            } else {
                _getInforUser.value = Resource.Error("Lost Internet Connection")
            }
        } catch (e: Exception) {
            Log.e("GETALLUSERS_API_ERROR", e.toString())
            _getInforUser.value = Resource.Error(e.toString())
        }
    }

    // Function to handle response for getting all user information
    private fun handleGetAllUsersResponse(response: Response<UserInforRequest>): Resource<UserInforRequest> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(user ?: resultResponse)
            }
        } else {
            Log.e("GETALLUSERS_RETROFIT_ERROR", response.toString())
        }
        return Resource.Error((user ?: response.message()).toString())
    }

    // Function to upload video
    fun uploadVideo(
        file: MultipartBody.Part
    ) {
        viewModelScope.launch {
            updateVideo(file)
        }
    }

    // Function to safely update video
    private suspend fun updateVideo(
        file: MultipartBody.Part
    ) {
        _uploadResult.value = Resource.Loading()
        try {
            if (NetworkUtil.hasInternetConnection(context)) {
                val response = repository.uploadVideo(file)
                _uploadResult.value = handleUploadResponse(response)
            } else {
                _uploadResult.value = Resource.Error("Lost Internet Connection")
            }
        } catch (e: Exception) {
            Log.d("ERROR_API", e.message.toString())
            _uploadResult.value = Resource.Error(e.message.toString())
        }
    }

    // Function to handle response for uploading video
    private fun handleUploadResponse(response: Response<String>): Resource<String> {
        if (response.isSuccessful) {
            LogUtils.d("LOGIN_RETROFIT_SUCCESS: OK")
            response.body()?.let { resultResponse ->
                return Resource.Success(res ?: resultResponse)
            }
        } else {
            var res = response.body().toString()
            if (response.code() == 401) res = "Error"
            else if (response.code() == 400) res = "Invalid request body"
            else if (response.code() == 500) res = "Internal server error"
            return Resource.Error(res)

            LogUtils.d("LOGIN_RETROFIT_ERROR: $response")
        }
        return Resource.Error((res ?: response.message()).toString())
    }

    // Function to change password
    fun changePassword(passwordPost: PasswordPost) = viewModelScope.launch {
        safeChangePassword(passwordPost)
    }

    // Function to safely change password
    private suspend fun safeChangePassword(passwordPost: PasswordPost) {
        try {
            if (hasInternetConnection(context)) {
                val response = repository.changePassword(passwordPost)
                if (response.isSuccessful) {
                    val hostResponse = response.body()
                    if (hostResponse != null) {
                        Toast.makeText(context, "Password changed successfully", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(context, "Connection error", Toast.LENGTH_LONG).show()
                }

            } else {
                // Handle no internet connection
            }
        } catch (e: Exception) {
            // Handle exceptions
        }
    }
}
