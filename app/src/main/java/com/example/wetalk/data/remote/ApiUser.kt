package com.example.wetalk.data.remote

import com.example.wetalk.data.model.objectmodel.AvatarRequest
import com.example.wetalk.data.model.objectmodel.GetAllUserFriendRequest
import com.example.wetalk.data.model.objectmodel.GetAllUserInforRequest
import com.example.wetalk.data.model.objectmodel.GetAllUserRequest
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
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path


interface ApiUser {
    //Generate Otp
    @POST("register/generateOtp")
    suspend fun generateOtp(@Body userRegisterDTO: UserRegisterDTO?): Response<HostResponse>
    @POST("register/validateOtp")
    //Validate Otp
    suspend  fun validateOtp(@Body userOtpDTO: UserOtpDTO): Response<HostResponse>
    //Login user
    @POST("auth/login")
    suspend  fun login(@Body loginDTO: LoginDTO): Response<LoginResponse>
    //Upload file
    @Multipart
    @POST("api/upload")
    suspend fun uploadVideo(
        @Part file: MultipartBody.Part)
            : Response<String>
    //Get UserInfor
    @GET("users/me")
    suspend fun geUserInfor(): Response<UserInforRequest>
    //UpdateUser
    @PUT("users")
    suspend fun updateUser(@Body userRequest: UserUpdateDTO
    ) : Response<GetAllUserRequest>
    //Upload Avatar
    @POST("users/uploadAvatar")
    suspend fun updateAvatar(@Body avatarRequest: AvatarRequest) : Response<GetAllUserRequest>
    //Change Password
    @POST("users/changePassword")
    suspend fun changePassword(@Body userPasswordDTO: UserPasswordDTO) : Response<HostResponse>
    //Search user
    @POST("users/search")
    suspend fun searchUser(@Body userQueryRequest: UserQueryRequest) : Response<GetAllUserInforRequest>
    //Get find friend and add friend
    @POST("friend-ship/add-friend/{userId}")
    suspend fun addFriend(@Path("userId") userId:Int) : Response<HostResponse>
    //Get list pending ship
    @GET("friend-ship/request-list")
    suspend fun pendingFriend() : Response<GetAllUserFriendRequest>
    //Get all friend
    @GET("/friend-ship/friend-list")
    suspend fun getAllFriend() :Response<GetAllUserFriendRequest>
    //Delete friend
    @GET("friend-ship/cancel-friend/{userId}")
    suspend fun getCancelFriend(@Path("userId") userId: Int) : Response<HostResponse>
    //Accept friend
    @POST("friend-ship/accept-friend/{userId}")
    suspend fun acceptFriend(@Path("userId") userId: Int): Response<HostResponse>
    @GET("users/{userId}")
    //Get user by Id
    suspend fun getUserById(@Part("userId") userId:Int) : Response<GetAllUserInforRequest>
    @DELETE("friend-ship/cancel-friend/{userId}")
    suspend fun deleteFriend(@Path("userId") userId: Int): Response<HostResponse>
}