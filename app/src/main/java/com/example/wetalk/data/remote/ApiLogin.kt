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
    //Generate Otp
    @POST("api/register/generateOtp")
    suspend fun generateOtp(@Body userRegisterDTO: UserRegisterDTO?): Response<HostResponse>
    @POST("api/register/validateOtp")
    //Validate Otp
    suspend  fun validateOtp(@Body userOtpDTO: UserOtpDTO): Response<HostResponse>
    //Login user
    @POST("api/auth/login")
    suspend  fun login(@Body loginDTO: LoginDTO): Response<LoginResponse>
    //Upload file
    @Multipart
    @POST("api/upload")
    suspend fun uploadVideo(
        @Part file: MultipartBody.Part)
            : Response<String>
    //Get UserInfor
    @GET("users/getUserInfor")
    suspend fun geUserInfor(@Header("Authorization") authorization: String): Response<UserInforRequest>
    //UpdateUser
    @PUT("users")
    suspend fun updateUser(@Header("Authorization")  authorization: String ,
                           @Body userRequest: UserUpdateDTO
    ) : Response<GetAllUserUpdate>
    //Upload Avatar
    @POST("users/uploadAvatar")
    suspend fun updateAvatar(@Header("Authorization")  authorization: String,@Body avatarRequest: AvatarRequest) : Response<GetAllUserUpdate>
    //Change Password
    @POST("users/changePassword")
    suspend fun changePassword(@Header("Authorization")  authorization: String, @Body userPasswordDTO: UserPasswordDTO) : Response<HostResponse>
    //Search user
    @POST("users/api/search")
    suspend fun searchUser(@Body userQueryRequest: UserQueryRequest) : Response<GetAllUserInforRequest>
    //Get find friend and add friend
    @GET("friend-ship/add-friend/{userId}")
    suspend fun addFriend(@Path("userId") userId:Int) : Response<HostResponse>
    //Get list pending ship
    @GET("friend-ship/pending")
    suspend fun pendingFriend(@Header("Authorization")  authorization: String)
    //Get all friend
    @GET("friend-ship/friend")
    suspend fun getAllFriend(@Header("Authorization")  authorization: String)
    //Delete friend
    @GET("friend-ship/cancel-friend/{userId}")
    suspend fun getCancelFriend(@Header("Authorization")  authorization: String, @Path("userId") userId: Int)
    //Accept friend
    @GET("friend-ship/accept-friend/{userId}")
    suspend fun acceptFriend(@Header("Authorization")  authorization: String,  @Path("userId") userId: Int)
}