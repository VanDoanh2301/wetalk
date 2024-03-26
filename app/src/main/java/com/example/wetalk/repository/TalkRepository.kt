package com.example.wetalk.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.example.wetalk.data.model.objectmodel.AvatarRequest
import com.example.wetalk.data.model.objectmodel.GetAllListConversations
import com.example.wetalk.data.model.objectmodel.GetAllQuestion
import com.example.wetalk.data.model.objectmodel.GetAllTopic
import com.example.wetalk.data.model.objectmodel.GetAllUserFriendRequest
import com.example.wetalk.data.model.objectmodel.GetAllUserInforRequest
import com.example.wetalk.data.model.objectmodel.GetAllUserRequest
import com.example.wetalk.data.model.objectmodel.GetAllVocabulariesByIdRequest
import com.example.wetalk.data.model.objectmodel.GetAllVocabulariesRequest
import com.example.wetalk.data.model.objectmodel.Message
import com.example.wetalk.data.model.objectmodel.MessagePaging
import com.example.wetalk.data.model.objectmodel.QuestionSize
import com.example.wetalk.data.model.objectmodel.RoomConversation
import com.example.wetalk.data.model.objectmodel.TopicRequest
import com.example.wetalk.data.model.postmodel.UserRegisterPost
import com.example.wetalk.data.model.objectmodel.UserInforRequest
import com.example.wetalk.data.model.objectmodel.QueryPageRequest
import com.example.wetalk.data.model.objectmodel.Question
import com.example.wetalk.data.model.objectmodel.VocabularyRequest
import com.example.wetalk.data.model.postmodel.UpdateUserPost
import com.example.wetalk.data.model.postmodel.LoginPost
import com.example.wetalk.data.model.postmodel.MediaValidatePost
import com.example.wetalk.data.model.postmodel.OtpPost
import com.example.wetalk.data.model.postmodel.PasswordPost
import com.example.wetalk.data.model.postmodel.VocabulariesDTO
import com.example.wetalk.data.model.responsemodel.LoginResponse
import com.example.wetalk.data.model.responsemodel.HostResponse
import com.example.wetalk.data.model.responsemodel.ValidationResponse
import com.example.wetalk.data.remote.ApiChat
import com.example.wetalk.data.remote.ApiUser
import com.example.wetalk.data.remote.ApiTopicStudy
import com.example.wetalk.data.remote.ApiUpload
import com.example.wetalk.data.remote.ApiValidation
import com.example.wetalk.paging.MessagePagingSource
import dagger.hilt.android.scopes.ViewModelScoped
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import javax.inject.Inject

@ViewModelScoped
class TalkRepository @Inject constructor(
    private val mUser: ApiUser,
    private val mUp: ApiUpload,
    private val mTopic: ApiTopicStudy,
    private val mChat: ApiChat,
    private val mValid: ApiValidation
) {
    suspend fun uploadVideo(file: MultipartBody.Part): Response<String> {
        return mUp.uploadVideo(file)
    }

    suspend fun getAllTopic(): Response<GetAllTopic> {
        return mTopic.getAllTopic()
    }

    suspend fun getAllQuestionByTopicId(topicId: Int): Response<GetAllQuestion> {
        return mTopic.getAllQuestionByTopicID(topicId)
    }

    suspend fun addTopic(@Body topicPost: TopicRequest): Response<GetAllTopic> {
        return mTopic.addTopic(topicPost)
    }

    suspend fun deleteTopic(@Path("id") id: Int): Response<GetAllTopic> {
        return mTopic.deleteTopic(id)
    }

    suspend fun searchTopic(@Body queryPageRequest: QueryPageRequest): Response<GetAllVocabulariesRequest> {
        return mTopic.searchTopic(queryPageRequest)
    }

    suspend fun updateTopic(topicRequest: TopicRequest): Response<GetAllTopic> {
        return mTopic.updateTopic(topicRequest)
    }

    suspend fun searchVocabularies(vocabulariesDTO: VocabulariesDTO): Response<GetAllVocabulariesRequest> {
        return mTopic.searchVocabularies(vocabulariesDTO)
    }

    suspend fun getVocabulariesById(topicId: Int): Response<GetAllVocabulariesByIdRequest> {
        return mTopic.getVocabulariesById(topicId)
    }

    suspend fun getCollectDataHistory(): Response<GetAllVocabulariesByIdRequest> {
        return mTopic.getCollectDataHistory()
    }

    suspend fun deleteVocabulary(id: Int): Response<HostResponse> {
        return mTopic.deleteVocabulary(id)
    }

    suspend fun addVocabulary(topicRequest: TopicRequest): Response<GetAllVocabulariesRequest> {
        return mTopic.addVocabulary(topicRequest)
    }

    suspend fun updateVocabulary(topicRequest: VocabularyRequest): Response<GetAllVocabulariesRequest> {
        return mTopic.updateVocabulary(topicRequest)
    }

    suspend fun getAllQuestionPageByTopicID(questionSize: QuestionSize): Response<GetAllQuestion> {
        return mTopic.getAllQuestionPageByTopicId(questionSize)
    }

    suspend fun createQuestion(question: Question): Response<GetAllQuestion> {
        return mTopic.createQuestion(question)
    }

    suspend fun updateQuestion(question: Question): Response<GetAllQuestion> {
      return  mTopic.updateQuestion(question)
    }
    suspend fun deleteQuestion(id :Int) : Response<GetAllQuestion> {
        return mTopic.deleteQuestion(id)
    }

    //User------------------------------------------------------------------------------------------
    suspend fun getUserInfor(): Response<UserInforRequest> {
        return mUser.geUserInfor()
    }

    suspend fun userLogin(post: LoginPost): Response<LoginResponse> {
        return mUser.login(post)
    }

    suspend fun generateOtp(userRegisterPost: UserRegisterPost): Response<HostResponse> {
        return mUser.generateOtp(userRegisterPost)
    }

    suspend fun validateOtp(otpPost: OtpPost): Response<HostResponse> {
        return mUser.validateOtp(otpPost)
    }

    suspend fun updateUser(userRequest: UpdateUserPost): Response<GetAllUserRequest> {
        return mUser.updateUser(userRequest)
    }

    suspend fun updateAvata(avatarRequest: AvatarRequest): Response<GetAllUserRequest> {
        return mUser.updateAvatar(avatarRequest)
    }

    suspend fun changePassword(passwordPost: PasswordPost): Response<HostResponse> {
        return mUser.changePassword(passwordPost)
    }

    suspend fun seacherUser(queryPageRequest: QueryPageRequest): Response<GetAllUserInforRequest> {
        return mUser.searchUser(queryPageRequest)
    }

    suspend fun addFriend(userId: Int): Response<HostResponse> {
        return mUser.addFriend(userId)
    }

    suspend fun getAllFriend(): Response<GetAllUserFriendRequest> {
        return mUser.getAllFriend()
    }

    suspend fun getCancelFriend(userId: Int): Response<HostResponse> {
        return mUser.getCancelFriend(userId)
    }

    suspend fun acceptFriend(userId: Int): Response<HostResponse> {
        return mUser.acceptFriend(userId)
    }

    suspend fun pendingFriend(): Response<GetAllUserFriendRequest> {
        return mUser.pendingFriend()
    }

    suspend fun deleteFriend(userId: Int): Response<HostResponse> {
        return mUser.deleteFriend(userId)
    }

    suspend fun getUserById(userId: Int): Response<GetAllUserInforRequest> {
        return mUser.getUserById(userId)
    }

    //Chat message----------------------------------------------------------------------------------
    suspend fun createRoom(roomConversation: RoomConversation): Response<HostResponse> {
        return mChat.createGroup(roomConversation)
    }

    suspend fun getAllConversations(): Response<List<GetAllListConversations>> {
        return mChat.getAllConversations()
    }

    suspend fun roomChat(contactId: Int): Response<GetAllListConversations> {
        return mChat.roomChat(contactId)
    }

    suspend fun getAllMessage(conversationId: Int): Response<List<Message>> {
        return mChat.getAllMessage(conversationId)
    }

    suspend fun getMessagesLimit(messagePaging: MessagePaging): Response<List<Message>> {
        return mChat.getMessagesLimit(messagePaging)
    }

    fun getLimitMessages(messagePaging: MessagePaging) = Pager(
        config = PagingConfig(pageSize = 10, maxSize = 100),
        pagingSourceFactory = { MessagePagingSource(mChat, messagePaging) }
    ).liveData

    //Ai Validation---------------------------------------------------------------------------------
    suspend fun validationMedia(mediaValidatePost: MediaValidatePost): Response<ValidationResponse> {
        return mValid.validationMedia(mediaValidatePost)
    }
}