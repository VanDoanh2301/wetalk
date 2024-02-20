package com.example.wetalk.repository

import com.example.wetalk.data.model.objectmodel.AvatarRequest
import com.example.wetalk.data.model.objectmodel.GetAllQuestion
import com.example.wetalk.data.model.objectmodel.GetAllTopic
import com.example.wetalk.data.model.objectmodel.GetAllUserInforRequest
import com.example.wetalk.data.model.objectmodel.GetAllUserUpdate
import com.example.wetalk.data.model.objectmodel.QuestionSize
import com.example.wetalk.data.model.postmodel.UserRegisterDTO
import com.example.wetalk.data.model.objectmodel.UserInforRequest
import com.example.wetalk.data.model.objectmodel.UserQueryRequest
import com.example.wetalk.data.model.postmodel.UserUpdateDTO
import com.example.wetalk.data.model.postmodel.LoginDTO
import com.example.wetalk.data.model.postmodel.UserOtpDTO
import com.example.wetalk.data.model.postmodel.UserPasswordDTO
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
    // Hàm đăng nhập người dùng
    suspend fun userLogin(post: LoginDTO): Response<LoginResponse> {
        return mLogin.login(post)
    }

    // Hàm tạo mã OTP
    suspend fun generateOtp(userRegisterDTO: UserRegisterDTO): Response<HostResponse> {
        return mLogin.generateOtp(userRegisterDTO)
    }

    // Hàm xác thực OTP
    suspend fun validateOtp(userOtpDTO: UserOtpDTO): Response<HostResponse> {
        return mLogin.validateOtp(userOtpDTO)
    }

    // Hàm tải lên video
    suspend fun uploadVideo(file: MultipartBody.Part): Response<String> {
        return mUp.uploadVideo(file)
    }

    // Hàm lấy tất cả các chủ đề
    suspend fun getAllTopic() : Response<GetAllTopic> {
        return mTopic.getAllTopic()
    }

    // Hàm lấy tất cả các câu hỏi theo ID chủ đề
    suspend fun getAllQuestionByTopicId(topicId:Int) : Response<GetAllQuestion> {
        return mTopic.getAllQuestionByTopicID(topicId)
    }

    // Hàm lấy thông tin người dùng
    suspend fun getUserInfor(authorization: String) : Response<UserInforRequest> {
        return mLogin.geUserInfor(authorization)
    }

    // Hàm cập nhật thông tin người dùng
    suspend fun updateUser(authorization: String, userRequest: UserUpdateDTO) : Response<GetAllUserUpdate> {
        return mLogin.updateUser(authorization,userRequest)
    }

    // Hàm lấy tất cả các câu hỏi theo ID chủ đề với kích thước câu hỏi
    suspend fun getAllQuestionByTopicID(questionSize: QuestionSize) : Response<GetAllQuestion> {
        return mTopic.getAllQuestionByTopicId(questionSize)
    }

    // Hàm cập nhật ảnh đại diện người dùng
    suspend fun updateAvata(authorization: String, avatarRequest: AvatarRequest) : Response<GetAllUserUpdate> {
        return mLogin.updateAvatar(authorization, avatarRequest)
    }

    // Hàm thay đổi mật khẩu người dùng
    suspend fun changePassword(authorization: String, userPasswordDTO: UserPasswordDTO) : Response<HostResponse> {
        return mLogin.changePassword(authorization, userPasswordDTO)
    }

    // Hàm tìm kiếm người dùng
    suspend fun seacherUser(userQueryRequest: UserQueryRequest) : Response<GetAllUserInforRequest> {
        return mLogin.searchUser(userQueryRequest)
    }

    // Hàm thêm bạn
    suspend fun addFriend(userId:Int) : Response<HostResponse> {
        return mLogin.addFriend(userId)
    }

}