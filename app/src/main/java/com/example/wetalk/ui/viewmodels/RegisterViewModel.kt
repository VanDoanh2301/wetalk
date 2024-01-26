package com.example.wetalk.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wetalk.data.model.postmodel.UserRegisterDTO
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
class RegisterViewModel @Inject constructor(
    private val repository:TalkRepository,
    @ApplicationContext val context: Context
) : ViewModel() {
    private val _registerResponseStateFlow: MutableStateFlow<Resource<HostResponse>> =
        MutableStateFlow(Resource.Loading())
    val registerResponseStateFlow: StateFlow<Resource<HostResponse>>
        get() = _registerResponseStateFlow

private val hostResponse:HostResponse ? =null
    fun generateOtp(userRegisterDTO: UserRegisterDTO) = viewModelScope.launch {
       safeGetAllUsers(userRegisterDTO)
    }
    private suspend fun safeGetAllUsers(userRegisterDTO: UserRegisterDTO) {
        try {
            if(NetworkUtil.hasInternetConnection(context)){
                val response = repository.generateOtp(userRegisterDTO)
                _registerResponseStateFlow.value = handleGetAllUsersResponse(response)
            } else {
                _registerResponseStateFlow.value = Resource.Error("Mất Kết Nối Internet")
            }
        } catch (e: Exception) {
            Log.e("GETALLUSERS_API_ERROR", e.toString())
            _registerResponseStateFlow.value = Resource.Error(e.toString())
        }
    }

    private fun handleGetAllUsersResponse(response: Response<HostResponse>): Resource<HostResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(hostResponse ?: resultResponse)
            }
        } else {
            Log.e("GETALLUSERS_RETROFIT_ERROR", response.toString())
        }
        return Resource.Error((hostResponse ?: response.message()).toString())
    }


}