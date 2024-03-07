package com.example.wetalk.repository

import com.example.wetalk.data.model.objectmodel.AvatarRequest
import com.example.wetalk.data.model.objectmodel.GetAllQuestion
import com.example.wetalk.data.model.objectmodel.GetAllTopic
import com.example.wetalk.data.model.objectmodel.GetAllUserFriendRequest
import com.example.wetalk.data.model.objectmodel.GetAllUserInforRequest
import com.example.wetalk.data.model.objectmodel.GetAllUserRequest
import com.example.wetalk.data.model.objectmodel.GetAllVocabulariesByIdRequest
import com.example.wetalk.data.model.objectmodel.GetAllVocabulariesRequest
import com.example.wetalk.data.model.objectmodel.QuestionSize
import com.example.wetalk.data.model.objectmodel.TopicRequest
import com.example.wetalk.data.model.postmodel.UserRegisterDTO
import com.example.wetalk.data.model.objectmodel.UserInforRequest
import com.example.wetalk.data.model.objectmodel.UserQueryRequest
import com.example.wetalk.data.model.postmodel.UserUpdateDTO
import com.example.wetalk.data.model.postmodel.LoginDTO
import com.example.wetalk.data.model.postmodel.UserOtpDTO
import com.example.wetalk.data.model.postmodel.UserPasswordDTO
import com.example.wetalk.data.model.postmodel.VocabulariesDTO
import com.example.wetalk.data.model.responsemodel.LoginResponse
import com.example.wetalk.data.model.responsemodel.HostResponse
import com.example.wetalk.data.remote.ApiUser
import com.example.wetalk.data.remote.ApiTopicStudy
import com.example.wetalk.data.remote.ApiUpload
import dagger.hilt.android.scopes.ViewModelScoped
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject

@ViewModelScoped
class TalkRepository @Inject constructor(
    private val mUser: ApiUser,
    private val mUp: ApiUpload,
    private val mTopic: ApiTopicStudy
) {
    // Hàm đăng nhập người dùng
    suspend fun userLogin(post: LoginDTO): Response<LoginResponse> {
        return mUser.login(post)
    }

    // Hàm tạo mã OTP
    suspend fun generateOtp(userRegisterDTO: UserRegisterDTO): Response<HostResponse> {
        return mUser.generateOtp(userRegisterDTO)
    }

    // Hàm xác thực OTP
    suspend fun validateOtp(userOtpDTO: UserOtpDTO): Response<HostResponse> {
        return mUser.validateOtp(userOtpDTO)
    }

    // Hàm tải lên video
    suspend fun uploadVideo(file: MultipartBody.Part): Response<String> {
        return mUp.uploadVideo(file)
    }

    // Hàm lấy tất cả các chủ đề
    suspend fun getAllTopic(): Response<GetAllTopic> {
        return mTopic.getAllTopic()
    }

    // Hàm lấy tất cả các câu hỏi theo ID chủ đề
    suspend fun getAllQuestionByTopicId(topicId: Int): Response<GetAllQuestion> {
        return mTopic.getAllQuestionByTopicID(topicId)
    }

    // Hàm lấy thông tin người dùng
    suspend fun getUserInfor(): Response<UserInforRequest> {
        return mUser.geUserInfor()
    }

    // Hàm cập nhật thông tin người dùng
    suspend fun updateUser(userRequest: UserUpdateDTO): Response<GetAllUserRequest> {
        return mUser.updateUser(userRequest)
    }

    // Hàm lấy tất cả các câu hỏi theo ID chủ đề với kích thước câu hỏi
    suspend fun getAllQuestionByTopicID(questionSize: QuestionSize): Response<GetAllQuestion> {
        return mTopic.getAllQuestionByTopicId(questionSize)
    }

    // Hàm cập nhật ảnh đại diện người dùng
    suspend fun updateAvata(avatarRequest: AvatarRequest): Response<GetAllUserRequest> {
        return mUser.updateAvatar(avatarRequest)
    }

    // Hàm thay đổi mật khẩu người dùng
    suspend fun changePassword(userPasswordDTO: UserPasswordDTO): Response<HostResponse> {
        return mUser.changePassword(userPasswordDTO)
    }

    // Hàm tìm kiếm người dùng
    suspend fun seacherUser(userQueryRequest: UserQueryRequest): Response<GetAllUserInforRequest> {
        return mUser.searchUser(userQueryRequest)
    }

    // Hàm thêm bạn
    suspend fun addFriend(userId: Int): Response<HostResponse> {
        return mUser.addFriend(userId)
    }

    //Lấy ra tất cả bạn bè
    suspend fun getAllFriend(): Response<GetAllUserFriendRequest> {
        return mUser.getAllFriend()
    }

    //Xóa bạn bè
    suspend fun getCancelFriend(userId: Int): Response<HostResponse> {
        return mUser.getCancelFriend(userId)
    }

    //Chấp nhận kết bạn
    suspend fun acceptFriend(userId: Int): Response<HostResponse>  {
        return mUser.acceptFriend(userId)
    }

    //Danh sách chờ kết bạn
    suspend fun pendingFriend(): Response<GetAllUserFriendRequest> {
        return mUser.pendingFriend()
    }

    //Get User by id
    suspend fun getUserById(userId: Int): Response<GetAllUserInforRequest> {
        return mUser.getUserById(userId)
    }

    suspend fun postVocabularies(topicDTO: TopicRequest): Response<HostResponse> {
        return mTopic.postVocabularies(topicDTO)
    }

    suspend fun searchVocabularies(vocabulariesDTO: VocabulariesDTO): Response<GetAllVocabulariesRequest> {
        return mTopic.searchVocabularies(vocabulariesDTO)
    }

    suspend fun getVocabulariesById(topicId: Int): Response<GetAllVocabulariesByIdRequest> {
        return mTopic.getVocabulariesById(topicId)
    }

    suspend fun deleteVocabularies(id: Int): Response<HostResponse> {
        return mTopic.deleteVocabularies(id)
    }

    suspend fun getCollectDataHistory(): Response<GetAllVocabulariesByIdRequest> {
        return mTopic.getCollectDataHistory()
    }


}