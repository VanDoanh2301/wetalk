package com.example.wetalk.data.remote

import com.example.wetalk.data.model.objectmodel.User
import com.example.wetalk.data.model.postmodel.LoginPost
import com.example.wetalk.data.model.postmodel.RegisterPost
import com.example.wetalk.data.model.responsemodel.Data
import com.example.wetalk.data.model.responsemodel.HostResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


interface ApiInterface {
    @POST("api/register/generateOtp")
    suspend fun generateOtp(@Body user: User?): Response<HostResponse>

    @POST("/api/register/validateOtp")
    suspend  fun validateOtp(@Body registerPost: RegisterPost): Response<HostResponse>

    @POST("/api/auth/login")
    suspend  fun login(@Body loginPost: LoginPost): Response<Data>


}