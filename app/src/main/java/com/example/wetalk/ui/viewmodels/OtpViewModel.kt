package com.example.wetalk.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wetalk.data.model.objectmodel.User
import com.example.wetalk.data.model.postmodel.LoginPost
import com.example.wetalk.data.model.postmodel.RegisterPost
import com.example.wetalk.data.model.responsemodel.Data
import com.example.wetalk.data.model.responsemodel.HostResponse
import com.example.wetalk.repository.TalkRepository
import com.example.wetalk.util.LogUtils
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
class OtpViewModel @Inject constructor(
    private val mRepository: TalkRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _otpResponseStateFlow: MutableStateFlow<Resource<HostResponse>> =
        MutableStateFlow(Resource.Loading())
    val otpResponseStateFlow: StateFlow<Resource<HostResponse>>
        get() = _otpResponseStateFlow

    private var res: HostResponse? = null


    fun validateOtp(registerPost: RegisterPost) = viewModelScope.launch {
       onValidateOtp(registerPost)
    }

    private suspend fun onValidateOtp(registerPost: RegisterPost) {
        _otpResponseStateFlow.value = Resource.Loading()
        try {
            if (NetworkUtil.hasInternetConnection(context)) {
                val response = mRepository.validateOtp(registerPost)
                _otpResponseStateFlow.value = handleValidOtpResponse(response)
            } else {
                _otpResponseStateFlow.value = Resource.Error("Mất Kết Nối Internet")
            }
        } catch (e: Exception) {
            LogUtils.d("LOGIN_API_ERROR: ${e.message}")
            _otpResponseStateFlow.value = Resource.Error("${e.message.toString()}")
        }
    }

    private fun handleValidOtpResponse(response: Response<HostResponse>): Resource<HostResponse> {
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Success(res ?: it)
            }
        } else {
            var res = response.body().toString()
            if (response.code() == 401) res = "Tài khoản đã tồn tại"
            else if (response.code() == 400) res = "Invalid request body"
            else if (response.code() == 500) res = "Internal server error"
            return Resource.Error(res)
        }
        return Resource.Error((res ?: response.message()).toString())
    }


}