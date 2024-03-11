package com.example.wetalk.data.remote

import com.example.wetalk.data.model.objectmodel.GetAllListConversations
import com.example.wetalk.data.model.objectmodel.RoomConversation
import com.example.wetalk.data.model.responsemodel.HostResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiChat {
    @POST("conversations")
    suspend fun createRoom(@Body roomConversation: RoomConversation) : Response<HostResponse>

    @GET("conversations/all-me")
    suspend fun getAllConversations() : Response<GetAllListConversations>

}
