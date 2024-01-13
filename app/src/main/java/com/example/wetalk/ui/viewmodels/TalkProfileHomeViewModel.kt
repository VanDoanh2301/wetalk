package com.example.wetalk.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wetalk.data.model.objectmodel.User
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
    private val _getInforUser: MutableStateFlow<Resource<User>> =
        MutableStateFlow(Resource.Loading())
    val getInforUser: StateFlow<Resource<User>>
        get() = _getInforUser

    private var user: User? = null

    fun getUser(authorization: String) = viewModelScope.launch() {
        safeGetAllUsers(authorization)
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

    private fun handleGetAllUsersResponse(response: Response<User>): Resource<User> {
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