package com.example.wetalk.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wetalk.data.model.objectmodel.GetInforUser
import com.example.wetalk.data.model.objectmodel.User
import com.example.wetalk.data.model.objectmodel.UserRequest
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
    private val _getInforUser: MutableStateFlow<Resource<UserRequest>> =
        MutableStateFlow(Resource.Loading())
    val getInforUser: StateFlow<Resource<UserRequest>>
        get() = _getInforUser

    private val _updateUser : MutableStateFlow<Resource<GetInforUser>> = MutableStateFlow(Resource.Loading())
    val updateUser : StateFlow<Resource<GetInforUser>> get() = _updateUser

    private var user: UserRequest? = null
    private var userInfor: GetInforUser ? =null


    fun getUser(authorization: String) = viewModelScope.launch() {
        safeGetAllUsers(authorization)
    }

    fun updateInforUser(userRequest: UserRequest) = viewModelScope.launch {
        safeUpdateUser(userRequest)
    }
    private suspend fun safeUpdateUser(userRequest: UserRequest) {

        try {
            if(hasInternetConnection(context)){
                val response =  repository.updateUser(userRequest)
                _updateUser.value = handleUpdateUsersResponse(response)
            } else {
                _updateUser.value = Resource.Error("Mất Kết Nối Internet")
            }
        } catch (e: Exception) {
            Log.e("GETALLUSERS_API_ERROR", e.toString())
            _getInforUser.value = Resource.Error(e.toString())
        }
    }

    private fun handleUpdateUsersResponse(response: Response<GetInforUser>): Resource<GetInforUser> {
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

    private fun handleGetAllUsersResponse(response: Response<UserRequest>): Resource<UserRequest> {
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