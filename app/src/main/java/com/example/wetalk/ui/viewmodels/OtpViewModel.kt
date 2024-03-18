package com.example.wetalk.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wetalk.data.model.postmodel.OtpPost
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
    // StateFlow for OTP validation response
    private val _otpResponseStateFlow: MutableStateFlow<Resource<HostResponse>> =
        MutableStateFlow(Resource.Loading())
    val otpResponseStateFlow: StateFlow<Resource<HostResponse>>
        get() = _otpResponseStateFlow

    private var res: HostResponse? = null

    // Function to validate OTP
    fun validateOtp(otpPost: OtpPost) = viewModelScope.launch {
        onValidateOtp(otpPost)
    }

    // Function to handle OTP validation
    private suspend fun onValidateOtp(otpPost: OtpPost) {
        _otpResponseStateFlow.value = Resource.Loading() // Set loading state
        try {
            // Check for internet connection
            if (NetworkUtil.hasInternetConnection(context)) {
                val response = mRepository.validateOtp(otpPost)
                // Handle OTP validation response
                _otpResponseStateFlow.value = handleValidOtpResponse(response)
            } else {
                _otpResponseStateFlow.value = Resource.Error("Lost Internet Connection") // Error state for no internet
            }
        } catch (e: Exception) {
            LogUtils.d("OTP_API_ERROR: ${e.message}")
            _otpResponseStateFlow.value = Resource.Error("${e.message.toString()}") // Error state for API failure
        }
    }

    // Function to handle OTP validation response
    private fun handleValidOtpResponse(response: Response<HostResponse>): Resource<HostResponse> {
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Success(res ?: it) // Success state with response data
            }
        } else {
            var res = response.body().toString()
            // Handle different HTTP error codes
            if (response.code() == 401) res = "Account already exists"
            else if (response.code() == 400) res = "Invalid request body"
            else if (response.code() == 500) res = "Internal server error"
            return Resource.Error(res) // Error state with error message
        }
        return Resource.Error((res ?: response.message()).toString()) // Default error state
    }
}
