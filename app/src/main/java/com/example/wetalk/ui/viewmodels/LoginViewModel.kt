package com.example.wetalk.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wetalk.data.model.postmodel.LoginPost
import com.example.wetalk.data.model.responsemodel.LoginResponse
import com.example.wetalk.repository.TalkRepository
import com.example.wetalk.util.LogUtils
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
class LoginViewModel @Inject constructor(
    private val mRepository: TalkRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _loginResponseStateFlow: MutableStateFlow<Resource<LoginResponse>> =
        MutableStateFlow(Resource.Loading())
    val loginResponseStateFlow: StateFlow<Resource<LoginResponse>>
        get() = _loginResponseStateFlow

    private var loginResponse: LoginResponse? = null


    /** Login */
    fun login(post: LoginPost) = viewModelScope.launch {
        safeLogin(post)
    }

    /** Check networking */
    private suspend fun safeLogin(post: LoginPost) {
        _loginResponseStateFlow.value = Resource.Loading()
        try {
            if (hasInternetConnection(context)) {
                val response = mRepository.userLogin(post)
                _loginResponseStateFlow.value = handleLoginResponse(response)
            } else {
                _loginResponseStateFlow.value = Resource.Error("Mất Kết Nối Internet")
            }
        } catch (e: Exception) {
            LogUtils.d("LOGIN_API_ERROR: ${e.message}")
            _loginResponseStateFlow.value = Resource.Error("${e.message.toString()}")
        }
    }

    /** Handle Login Response */
    private fun handleLoginResponse(response: Response<LoginResponse>): Resource<LoginResponse> {
        if (response.isSuccessful) {
            LogUtils.d("LOGIN_RETROFIT_SUCCESS: OK")
            response.body()?.let { resultResponse ->
                return Resource.Success(loginResponse ?: resultResponse)
            }
        } else {
            var res = response.body().toString()
            if (response.code() == 401) res = "Email Hoặc Mật Khẩu Không Đúng"
            else if (response.code() == 400) res = "Invalid request body"
            else if (response.code() == 500) res = "Internal server error"
            return Resource.Error(res)

            LogUtils.d("LOGIN_RETROFIT_ERROR: $response")
        }
        return Resource.Error((loginResponse ?: response.message()).toString())
    }



}