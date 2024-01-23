package com.example.wetalk.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wetalk.data.model.objectmodel.AvatarRequest
import com.example.wetalk.data.model.objectmodel.GetAllUserUpdate
import com.example.wetalk.data.model.objectmodel.UserInforRequest
import com.example.wetalk.data.model.objectmodel.UserUpdate
import com.example.wetalk.repository.TalkRepository
import com.example.wetalk.util.NetworkUtil.Companion.hasInternetConnection
import com.example.wetalk.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class TalkProfileHomeViewModel @Inject constructor(private val repository: TalkRepository,
                                                   @ApplicationContext private val context: Context) :ViewModel(){
    private val _getInforUser: MutableStateFlow<Resource<UserInforRequest>> =
        MutableStateFlow(Resource.Loading())
    val getInforUser: StateFlow<Resource<UserInforRequest>>
        get() = _getInforUser

    private val _updateUser : MutableStateFlow<Resource<GetAllUserUpdate>> = MutableStateFlow(Resource.Loading())
    val updateUser : StateFlow<Resource<GetAllUserUpdate>> get() = _updateUser

    private val _updateAvatar : MutableStateFlow<Resource<GetAllUserUpdate>> = MutableStateFlow(Resource.Loading())
    val updateAvatar : StateFlow<Resource<GetAllUserUpdate>> get() = _updateAvatar

    private var user: UserInforRequest? = null
    private var userInfor: GetAllUserUpdate ? =null


    fun getUser(authorization: String) = viewModelScope.launch() {
        safeGetAllUsers(authorization)
    }

    fun updateAvatarUser(authorization: String,avatarRequest: AvatarRequest) = viewModelScope.launch {
        safeUserAvatar(authorization,avatarRequest)
    }
    private suspend fun safeUserAvatar(authorization: String,avatarRequest: AvatarRequest) {

        try {
            if(hasInternetConnection(context)){
                val response =  repository.updateAvata(authorization,avatarRequest)
                _updateAvatar.value = handleUpdateAvatarResponse(response)
            } else {
                _updateAvatar.value = Resource.Error("Mất Kết Nối Internet")
            }
        } catch (e: Exception) {
            Log.e("GETALLUSERS_API_ERROR", e.toString())
            _getInforUser.value = Resource.Error(e.toString())
        }
    }

    private fun handleUpdateAvatarResponse(response: Response<GetAllUserUpdate>): Resource<GetAllUserUpdate> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(userInfor ?: resultResponse)
            }
        } else {
            Log.e("GETALLUSERS_RETROFIT_ERROR", response.toString())
        }
        return Resource.Error((userInfor ?: response.message()).toString())
    }


    fun updateInforUser(authorization: String,userRequest: UserUpdate) = viewModelScope.launch {
        safeUpdateUser(authorization,userRequest)
    }
    private suspend fun safeUpdateUser(authorization: String,userRequest: UserUpdate) {

        try {
            if(hasInternetConnection(context)){
                val response =  repository.updateUser(authorization,userRequest)
                _updateUser.value = handleUpdateUsersResponse(response)
            } else {
                _updateUser.value = Resource.Error("Mất Kết Nối Internet")
            }
        } catch (e: Exception) {
            Log.e("GETALLUSERS_API_ERROR", e.toString())
            _getInforUser.value = Resource.Error(e.toString())
        }
    }

    private fun handleUpdateUsersResponse(response: Response<GetAllUserUpdate>): Resource<GetAllUserUpdate> {
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
            if(hasInternetConnection(context)){
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


}