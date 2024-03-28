package com.example.wetalk.data.remote

import com.example.wetalk.data.model.objectmodel.GetAllCollectData
import com.example.wetalk.data.model.objectmodel.GetAllCollectDataSearch
import com.example.wetalk.data.model.objectmodel.GetAllQuestion
import com.example.wetalk.data.model.objectmodel.GetAllTopic
import com.example.wetalk.data.model.objectmodel.GetAllVocabulariesByIdRequest
import com.example.wetalk.data.model.objectmodel.GetAllVocabulariesRequest
import com.example.wetalk.data.model.objectmodel.QueryPageRequest
import com.example.wetalk.data.model.objectmodel.Question
import com.example.wetalk.data.model.objectmodel.QuestionSize
import com.example.wetalk.data.model.objectmodel.TopicRequest
import com.example.wetalk.data.model.objectmodel.VocabularyRequest
import com.example.wetalk.data.model.postmodel.DataApprovedPost
import com.example.wetalk.data.model.postmodel.DataPost
import com.example.wetalk.data.model.postmodel.DataPostSearch
import com.example.wetalk.data.model.postmodel.QuestionPost
import com.example.wetalk.data.model.postmodel.VocabulariesDTO
import com.example.wetalk.data.model.responsemodel.HostResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiTopicStudy {
    @GET("topics/all")
    suspend fun getAllTopic() : Response<GetAllTopic>
    @GET("questions/{topicId}")
    suspend fun getAllQuestionByTopicID(@Path("topicId") topicId:Int) : Response<GetAllQuestion>
    @POST("questions")
    suspend fun createQuestion(@Body question: QuestionPost) : Response<GetAllQuestion>
    @PUT("questions")
    suspend fun updateQuestion(@Body question: Question) : Response<GetAllQuestion>
    @DELETE("questions/{id}")
    suspend fun deleteQuestion(@Path("id") id :Int) : Response<GetAllQuestion>
    @POST("questions/limits-topic")
    suspend fun getAllQuestionPageByTopicId(@Body questionSize: QuestionSize) : Response<GetAllQuestion>
    @POST("vocabularies/search")
    suspend fun searchVocabularies(@Body vocabulariesDTO: VocabulariesDTO) : Response<GetAllVocabulariesRequest>
    @GET("vocabularies/{topicId}")
    suspend fun getVocabulariesById(@Path("topicId") topicId:Int)  : Response<GetAllVocabulariesByIdRequest>
    @DELETE("vocabularies/{id}")
    suspend fun deleteVocabulary(@Path("id") id:Int) : Response<HostResponse>
    @GET("data-collection/all-me")
    suspend fun getCollectDataMe() : Response<GetAllCollectData>
    @POST("topics")
    suspend fun addTopic(@Body topicPost: TopicRequest) : Response<GetAllTopic>
    @DELETE("topics/{id}")
    suspend fun deleteTopic(@Path("id")  id:Int) : Response<GetAllTopic>
    @PUT("topics")
    suspend fun updateTopic(@Body topicRequest: TopicRequest) : Response<GetAllTopic>
    @POST("topics/search")
    suspend fun searchTopic(@Body queryPageRequest: QueryPageRequest) : Response<GetAllVocabulariesRequest>
    @POST("vocabularies")
    suspend fun addVocabulary(@Body topicRequest: TopicRequest) : Response<GetAllVocabulariesRequest>
    @PUT("vocabularies")
    suspend fun updateVocabulary(@Body topicRequest: VocabularyRequest) : Response<GetAllVocabulariesRequest>
    @GET("data-collection/pending-list-admin")
    suspend fun getAllDataPending() : Response<GetAllCollectData>
    @GET("data-collection/all-me")
    suspend fun getAllDataMe() : Response<GetAllCollectData>
    @POST("data-collection/approve/{id}")
    suspend fun approveDataById(@Path("id") id:Int) : Response<GetAllCollectData>
    @DELETE("data-collection/{id}")
    suspend fun deleteDataById(@Path("id") id:Int) : Response<GetAllCollectData>
    @POST("data-collection/reject")
    suspend fun rejectCollectData(@Body dataApprovedPost: DataApprovedPost) : Response<GetAllCollectData>
    @GET("data-collection/approved-list-me")
    suspend fun getAllApprovedListMe() : Response<GetAllCollectData>
    @GET("data-collection/reject-list-me")
    suspend fun getAllRejectListMe() : Response<GetAllCollectData>
    @POST("data-collection")
    suspend fun postDataCollection(@Body dataPost: DataPost) : Response<GetAllCollectData>
    @POST("data-collection/search-for-me")
    suspend fun searchDataCollectionMe(@Body dataPostSearch: DataPostSearch) : Response<GetAllCollectDataSearch>


}