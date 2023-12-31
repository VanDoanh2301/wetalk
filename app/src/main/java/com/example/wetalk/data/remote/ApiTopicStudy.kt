package com.example.wetalk.data.remote

import com.example.wetalk.data.model.objectmodel.GetAllQuestion
import com.example.wetalk.data.model.objectmodel.GetAllTopic
import com.example.wetalk.data.model.responsemodel.HostResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiTopicStudy {
    @GET("topics")
    suspend fun getAllTopic() : Response<GetAllTopic>

    @GET("questions/{topicId}")
    suspend fun getAllQuestionByTopicID(@Path("topicId") topicId:Int) : Response<GetAllQuestion>
}