package com.example.wetalk.data.remote

import com.example.wetalk.data.model.objectmodel.User
import com.example.wetalk.data.model.postmodel.LoginPost
import com.example.wetalk.data.model.postmodel.RegisterPost
import com.example.wetalk.data.model.responsemodel.LoginResponse
import com.example.wetalk.data.model.responsemodel.HostResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part


interface ApiLogin {
    @POST("api/register/generateOtp")
    suspend fun generateOtp(@Body user: User?): Response<HostResponse>

    @POST("api/register/validateOtp")
    suspend  fun validateOtp(@Body registerPost: RegisterPost): Response<HostResponse>

    @POST("api/auth/login")
    suspend  fun login(@Body loginPost: LoginPost): Response<LoginResponse>
    @Multipart
    @POST("api/upload")
    suspend fun uploadVideo(
        @Part file: MultipartBody.Part)
            : Response<String>

}