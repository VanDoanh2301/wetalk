package com.example.wetalk.data.remote

import com.example.wetalk.data.model.objectmodel.AvatarRequest
import com.example.wetalk.data.model.objectmodel.GetAllUserInforRequest
import com.example.wetalk.data.model.objectmodel.GetAllUserUpdate
import com.example.wetalk.data.model.postmodel.UserRegisterDTO
import com.example.wetalk.data.model.objectmodel.UserInforRequest
import com.example.wetalk.data.model.objectmodel.UserQueryRequest
import com.example.wetalk.data.model.postmodel.UserUpdateDTO
import com.example.wetalk.data.model.postmodel.LoginDTO
import com.example.wetalk.data.model.postmodel.UserOtpDTO
import com.example.wetalk.data.model.postmodel.UserPasswordDTO
import com.example.wetalk.data.model.responsemodel.LoginResponse
import com.example.wetalk.data.model.responsemodel.HostResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path


interface ApiLogin {
    @POST("api/register/generateOtp")
    suspend fun generateOtp(@Body userRegisterDTO: UserRegisterDTO?): Response<HostResponse>

    @POST("api/register/validateOtp")
    suspend  fun validateOtp(@Body userOtpDTO: UserOtpDTO): Response<HostResponse>

    @POST("api/auth/login")
    suspend  fun login(@Body loginDTO: LoginDTO): Response<LoginResponse>
    @Multipart
    @POST("api/upload")
    suspend fun uploadVideo(
        @Part file: MultipartBody.Part)
            : Response<String>

    @GET("users/getUserInfor")
    suspend fun geUserInfor(@Header("Authorization") authorization: String): Response<UserInforRequest>

    @PUT("users")
    suspend fun updateUser(@Header("Authorization")  authorization: String ,
                           @Body userRequest: UserUpdateDTO
    ) : Response<GetAllUserUpdate>
    @POST("users/uploadAvatar")
    suspend fun updateAvatar(@Header("Authorization")  authorization: String,@Body avatarRequest: AvatarRequest) : Response<GetAllUserUpdate>

    @POST("users/changePassword")
    suspend fun changePassword(@Header("Authorization")  authorization: String, @Body userPasswordDTO: UserPasswordDTO) : Response<HostResponse>

    @POST("users/api/search")
    suspend fun searchUser(@Body userQueryRequest: UserQueryRequest) : Response<GetAllUserInforRequest>

    @GET("friend-ship/add-friend/{userId}")
    suspend fun addFriend(@Path("userId") userId:Int) : Response<HostResponse>


}