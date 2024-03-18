package com.example.wetalk.data.remote

import com.example.wetalk.data.model.objectmodel.AvatarRequest
import com.example.wetalk.data.model.objectmodel.GetAllUserFriendRequest
import com.example.wetalk.data.model.objectmodel.GetAllUserInforRequest
import com.example.wetalk.data.model.objectmodel.GetAllUserRequest
import com.example.wetalk.data.model.postmodel.UserRegisterPost
import com.example.wetalk.data.model.objectmodel.UserInforRequest
import com.example.wetalk.data.model.objectmodel.UserQueryRequest
import com.example.wetalk.data.model.postmodel.UpdateUserPost
import com.example.wetalk.data.model.postmodel.LoginPost
import com.example.wetalk.data.model.postmodel.OtpPost
import com.example.wetalk.data.model.postmodel.PasswordPost
import com.example.wetalk.data.model.responsemodel.LoginResponse
import com.example.wetalk.data.model.responsemodel.HostResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path


interface ApiUser {
    //Generate Otp
    @POST("register/generate-otp")
    suspend fun generateOtp(@Body userRegisterPost: UserRegisterPost?): Response<HostResponse>
    @POST("register/validate-otp")
    //Validate Otp
    suspend  fun validateOtp(@Body otpPost: OtpPost): Response<HostResponse>
    //Login user
    @POST("auth/login")
    suspend  fun login(@Body loginPost: LoginPost): Response<LoginResponse>
    //Get UserInfor
    @GET("users/me")
    suspend fun geUserInfor(): Response<UserInforRequest>
    //UpdateUser
    @PUT("users")
    suspend fun updateUser(@Body userRequest: UpdateUserPost
    ) : Response<GetAllUserRequest>
    //Upload Avatar
    @POST("users/uploadAvatar")
    suspend fun updateAvatar(@Body avatarRequest: AvatarRequest) : Response<GetAllUserRequest>
    //Change Password
    @POST("users/changePassword")
    suspend fun changePassword(@Body passwordPost: PasswordPost) : Response<HostResponse>
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