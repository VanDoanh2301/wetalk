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
import com.example.wetalk.data.model.postmodel.UserPasswordDTO
import com.example.wetalk.data.model.postmodel.UserUpdateDTO
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
class TalkProfileHomeViewModel @Inject constructor(
    private val repository: TalkRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _getInforUser: MutableStateFlow<Resource<UserInforRequest>> =
        MutableStateFlow(Resource.Loading())
    val getInforUser: StateFlow<Resource<UserInforRequest>> get() = _getInforUser
    private val _updateUser: MutableStateFlow<Resource<GetAllUserRequest>> =
        MutableStateFlow(Resource.Loading())
    val updateUser: StateFlow<Resource<GetAllUserRequest>> get() = _updateUser
    private val _updateAvatar: MutableStateFlow<Resource<GetAllUserRequest>> =
        MutableStateFlow(Resource.Loading())
    val updateAvatar: StateFlow<Resource<GetAllUserRequest>> get() = _updateAvatar
    private val _uploadResult: MutableLiveData<Resource<String>> =
        MutableLiveData(Resource.Loading())
    val uploadResult: LiveData<Resource<String>> get() = _uploadResult


    private var user: UserInforRequest? = null
    private var userInfor: GetAllUserRequest? = null
    private var res: String? = null


    fun getUser(authorization: String) = viewModelScope.launch() {
        safeGetAllUsers(authorization)
    }

    fun updateAvatarUser(authorization: String, avatarRequest: AvatarRequest) =
        viewModelScope.launch {
            safeUserAvatar(authorization, avatarRequest)
        }

    private suspend fun safeUserAvatar(authorization: String, avatarRequest: AvatarRequest) {

        try {
            if (hasInternetConnection(context)) {
                val response = repository.updateAvata(authorization, avatarRequest)
                _updateAvatar.value = handleUpdateAvatarResponse(response)
            } else {
                _updateAvatar.value = Resource.Error("Mất Kết Nối Internet")
            }
        } catch (e: Exception) {
            Log.e("GETALLUSERS_API_ERROR", e.toString())
            _getInforUser.value = Resource.Error(e.toString())
        }
    }

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


    fun updateInforUser(authorization: String, userRequest: UserUpdateDTO) = viewModelScope.launch {
        safeUpdateUser(authorization, userRequest)
    }

    private suspend fun safeUpdateUser(authorization: String, userRequest: UserUpdateDTO) {

        try {
            if (hasInternetConnection(context)) {
                val response = repository.updateUser(authorization, userRequest)
                _updateUser.value = handleUpdateUsersResponse(response)
            } else {
                _updateUser.value = Resource.Error("Mất Kết Nối Internet")
            }
        } catch (e: Exception) {
            Log.e("GETALLUSERS_API_ERROR", e.toString())
            _getInforUser.value = Resource.Error(e.toString())
        }
    }

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

    private suspend fun safeGetAllUsers(authorization: String) {
        try {
            if (hasInternetConnection(context)) {
                val response = repository.getUserInfor(authorization)
                _getInforUser.value = handleGetAllUsersResponse(response)
            } else {
                _getInforUser.value = Resource.Error("Mất Kết Nối Internet")
            }
        } catch (e: Exception) {
            Log.e("GETALLUSERS_API_ERROR", e.toString())
            _getInforUser.value = Resource.Error(e.toString())
        }
    }

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


    fun uploadVideo(
        file: MultipartBody.Part)
    {
        viewModelScope.launch {
            updateVideo(file)
        }

    }

    private suspend fun updateVideo(
        file: MultipartBody.Part
    ) {
        _uploadResult.value = Resource.Loading()
        try {
            if (NetworkUtil.hasInternetConnection(context)) {
                val response = repository.uploadVideo(file)
                _uploadResult.value = handleUploadResponse(response)
            } else {
                _uploadResult.value = Resource.Error("Mất Kết Nối Internet")
            }
        } catch (e: Exception) {
            Log.d("ERROR_API", e.message.toString())
            _uploadResult.value = Resource.Error(e.message.toString())
        }
    }

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

    fun changePassword(authorization: String, userPasswordDTO: UserPasswordDTO) = viewModelScope.launch {
        safeChangePassword(authorization, userPasswordDTO)
    }

    private suspend fun safeChangePassword(authorization: String, userPasswordDTO: UserPasswordDTO)  {
        try {
            if (hasInternetConnection(context)) {
                val response = repository.changePassword(authorization, userPasswordDTO)
                if (response.isSuccessful) {
                    val hostResponse = response.body()
                    if (hostResponse != null) {
                        Toast.makeText(context, "Thay đổi mật khẩu thành công", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(context, "Lỗi kết nối", Toast.LENGTH_LONG).show()
                }

            } else {

            }
        } catch (e: Exception) {

        }
    }

}