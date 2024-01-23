package com.example.wetalk.repository

import com.example.wetalk.data.model.objectmodel.AvatarRequest
import com.example.wetalk.data.model.objectmodel.GetAllQuestion
import com.example.wetalk.data.model.objectmodel.GetAllTopic
import com.example.wetalk.data.model.objectmodel.GetAllUserInforRequest
import com.example.wetalk.data.model.objectmodel.GetAllUserUpdate
import com.example.wetalk.data.model.objectmodel.QuestionSize
import com.example.wetalk.data.model.objectmodel.UserRegisterRequest
import com.example.wetalk.data.model.objectmodel.UserInforRequest
import com.example.wetalk.data.model.objectmodel.UserQueryRequest
import com.example.wetalk.data.model.objectmodel.UserUpdate
import com.example.wetalk.data.model.postmodel.LoginPost
import com.example.wetalk.data.model.postmodel.RegisterPost
import com.example.wetalk.data.model.responsemodel.LoginResponse
import com.example.wetalk.data.model.responsemodel.HostResponse
import com.example.wetalk.data.remote.ApiLogin
import com.example.wetalk.data.remote.ApiTopicStudy
import com.example.wetalk.data.remote.ApiUpload
import dagger.hilt.android.scopes.ViewModelScoped
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject

@ViewModelScoped
class TalkRepository @Inject constructor(
    private val mLogin: ApiLogin,
    private val mUp: ApiUpload,
    private val mTopic: ApiTopicStudy
) {
    suspend fun userLogin(post: LoginPost): Response<LoginResponse> {
        return mLogin.login(post)
    }
    suspend fun generateOtp(userRegisterRequest: UserRegisterRequest): Response<HostResponse> {
        return mLogin.generateOtp(userRegisterRequest)
    }
    suspend fun validateOtp(registerPost: RegisterPost): Response<HostResponse> {
        return mLogin.validateOtp(registerPost)
    }
    suspend fun uploadVideo(file: MultipartBody.Part):
            Response<String> {
        return mUp.uploadVideo(file)
    }
    suspend fun getAllTopic() : Response<GetAllTopic> {
        return mTopic.getAllTopic()
    }
    suspend fun getAllQuestionByTopicId(topicId:Int) : Response<GetAllQuestion> {
        return mTopic.getAllQuestionByTopicID(topicId)
    }
    suspend fun getUserInfor(authorization: String) : Response<UserInforRequest> {
        return mLogin.geUserInfor(authorization)
    }
    suspend fun updateUser(authorization: String,userRequest: UserUpdate) : Response<GetAllUserUpdate> {
        return mLogin.updateUser(authorization,userRequest)
    }
    suspend fun getAllQuestionByTopicID(questionSize: QuestionSize) : Response<GetAllQuestion> {
        return mTopic.getAllQuestionByTopicId(questionSize)
    }
    suspend fun updateAvata(authorization: String, avatarRequest: AvatarRequest) : Response<GetAllUserUpdate> {
        return mLogin.updateAvatar(authorization, avatarRequest)
    }
    suspend fun seacherUser(userQueryRequest: UserQueryRequest) : Response<GetAllUserInforRequest> {
        return mLogin.searchUser(userQueryRequest)
    }
    suspend fun addFriend(userId:Int) : Response<HostResponse> {
        return mLogin.addFriend(userId)
    }

}