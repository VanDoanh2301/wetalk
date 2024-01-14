package com.example.wetalk.repository

import com.example.wetalk.data.model.objectmodel.GetAllQuestion
import com.example.wetalk.data.model.objectmodel.GetAllTopic
import com.example.wetalk.data.model.objectmodel.GetInforUser
import com.example.wetalk.data.model.objectmodel.QuestionSize
import com.example.wetalk.data.model.objectmodel.User
import com.example.wetalk.data.model.objectmodel.UserRequest
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
    suspend fun generateOtp(user: User): Response<HostResponse> {
        return mLogin.generateOtp(user)
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
    suspend fun getUserInfor(authorization: String) : Response<UserRequest> {
        return mLogin.geUserInfor(authorization)
    }
    suspend fun updateUser(userRequest: UserRequest) : Response<GetInforUser> {
        return mLogin.updateUser(userRequest)
    }
    suspend fun getAllQuestionByTopicID(questionSize: QuestionSize) : Response<GetAllQuestion> {
        return mTopic.getAllQuestionByTopicId(questionSize)
    }

}