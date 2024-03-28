package com.example.wetalk.data.remote

import com.example.wetalk.data.model.objectmodel.GetAllChatMessage
import com.example.wetalk.data.model.objectmodel.GetAllListConversations
import com.example.wetalk.data.model.objectmodel.Message
import com.example.wetalk.data.model.objectmodel.MessagePaging
import com.example.wetalk.data.model.objectmodel.RoomConversation
import com.example.wetalk.data.model.responsemodel.HostResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiChat {
    @POST("conversations")
    suspend fun createGroup(@Body roomConversation: RoomConversation) : Response<HostResponse>

    @GET("conversations/all-me")
    suspend fun getAllConversations() : Response<GetAllListConversations>

    @GET("conversations/{contactId}")
    suspend fun roomChat(@Path("contactId") contactId:Int) : Response<GetAllListConversations>

    @GET("messages/{conversationId}")
    suspend fun getAllMessage(@Path("conversationId") conversationId:Int) : Response<GetAllChatMessage>

    @POST("messages/limits-conversation")
    suspend fun getMessagesLimit(@Body messagePaging: MessagePaging) : Response<List<Message>>
}
