package com.example.wetalk.repository

import com.example.wetalk.data.model.objectmodel.User
import com.example.wetalk.data.model.postmodel.LoginPost
import com.example.wetalk.data.model.postmodel.RegisterPost
import com.example.wetalk.data.model.responsemodel.Data
import com.example.wetalk.data.model.responsemodel.HostResponse
import com.example.wetalk.data.remote.ApiInterface
import dagger.hilt.android.scopes.ViewModelScoped
import retrofit2.Response
import javax.inject.Inject

@ViewModelScoped
class TalkRepository @Inject constructor(private val mApi:ApiInterface,
    ) {
    suspend fun userLogin(post: LoginPost): Response<Data> {
        return mApi.login(post)
    }
    suspend fun generateOtp(user: User) : Response<HostResponse> {
        return mApi.generateOtp(user)
    }
    suspend fun validateOtp(registerPost: RegisterPost) : Response<HostResponse> {
        return mApi.validateOtp(registerPost)
    }

}