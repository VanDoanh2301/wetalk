package com.example.wetalk.data.remote

import com.example.wetalk.data.model.objectmodel.GetAllQuestion
import com.example.wetalk.data.model.objectmodel.GetAllTopic
import com.example.wetalk.data.model.objectmodel.GetAllVocabulariesByIdRequest
import com.example.wetalk.data.model.objectmodel.GetAllVocabulariesRequest
import com.example.wetalk.data.model.objectmodel.QuestionSize
import com.example.wetalk.data.model.objectmodel.TopicRequest
import com.example.wetalk.data.model.postmodel.VocabulariesDTO
import com.example.wetalk.data.model.responsemodel.HostResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiTopicStudy {
    @GET("topics")
    suspend fun getAllTopic() : Response<GetAllTopic>
    @GET("questions/{topicId}")
    suspend fun getAllQuestionByTopicID(@Path("topicId") topicId:Int) : Response<GetAllQuestion>
    @POST("questions/get-by-topic")
    suspend fun getAllQuestionByTopicId(@Body questionSize: QuestionSize) : Response<GetAllQuestion>
    @POST("vocabularies")
    suspend fun postVocabularies(@Body topicDTO: TopicRequest) : Response<HostResponse>
    @POST("vocabularies/api/search")
    suspend fun searchVocabularies(@Body vocabulariesDTO: VocabulariesDTO) : Response<GetAllVocabulariesRequest>
    @GET("vocabularies/{topicId}")
    suspend fun getVocabulariesById(@Path("topicId") topicId:Int)  : Response<GetAllVocabulariesByIdRequest>
    @DELETE("/vocabularies/{id}")
    suspend fun deleteVocabularies(@Path("Id") id:Int) : Response<HostResponse>
    @GET("collect-data/get-history")
    suspend fun getCollectData() : Response<GetAllVocabulariesByIdRequest>

}