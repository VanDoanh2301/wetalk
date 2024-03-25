package com.example.wetalk.data.remote

import com.example.wetalk.data.model.objectmodel.GetAllQuestion
import com.example.wetalk.data.model.objectmodel.GetAllTopic
import com.example.wetalk.data.model.objectmodel.GetAllVocabulariesByIdRequest
import com.example.wetalk.data.model.objectmodel.GetAllVocabulariesRequest
import com.example.wetalk.data.model.objectmodel.QueryPageRequest
import com.example.wetalk.data.model.objectmodel.QuestionSize
import com.example.wetalk.data.model.objectmodel.TopicRequest
import com.example.wetalk.data.model.postmodel.TopicPost
import com.example.wetalk.data.model.postmodel.VocabulariesDTO
import com.example.wetalk.data.model.responsemodel.HostResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiTopicStudy {
    @GET("topics/all")
    suspend fun getAllTopic() : Response<GetAllTopic>
    @GET("questions/{topicId}")
    suspend fun getAllQuestionByTopicID(@Path("topicId") topicId:Int) : Response<GetAllQuestion>
    @POST("questions/limits-topic")
    suspend fun getAllQuestionByTopicId(@Body questionSize: QuestionSize) : Response<GetAllQuestion>
    @POST("vocabularies")
    suspend fun postVocabularies(@Body topicDTO: TopicRequest) : Response<HostResponse>
    @POST("vocabularies/search")
    suspend fun searchVocabularies(@Body vocabulariesDTO: VocabulariesDTO) : Response<GetAllVocabulariesRequest>
    @GET("vocabularies/{topicId}")
    suspend fun getVocabulariesById(@Path("topicId") topicId:Int)  : Response<GetAllVocabulariesByIdRequest>
    @DELETE("/vocabularies/{id}")
    suspend fun deleteVocabularies(@Path("Id") id:Int) : Response<HostResponse>
    @GET("collect-data/all-me")
    suspend fun getCollectDataHistory() : Response<GetAllVocabulariesByIdRequest>
    @POST("topics")
    suspend fun addTopic(@Body topicPost: TopicPost) : Response<GetAllTopic>
    @DELETE("topics/{id}")
    suspend fun deleteTopic(@Path("id")  id:Int) : Response<GetAllTopic>
    @POST("topics/search")
    suspend fun searchTopic(@Body queryPageRequest: QueryPageRequest) : Response<GetAllVocabulariesRequest>

}