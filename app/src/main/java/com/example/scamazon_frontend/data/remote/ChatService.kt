package com.example.scamazon_frontend.data.remote

import com.example.scamazon_frontend.data.models.chat.ChatConversationsResponseDto
import com.example.scamazon_frontend.data.models.chat.ChatMessagesResponseDto
import com.example.scamazon_frontend.data.models.chat.SendMessageRequestDto
import com.example.scamazon_frontend.data.models.chat.SendMessageResponseDto
import com.example.scamazon_frontend.data.models.chat.StartChatRequestDto
import com.example.scamazon_frontend.data.models.chat.StartChatResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ChatService {
    @POST("api/chat/start")
    suspend fun startChat(@Body request: StartChatRequestDto): Response<StartChatResponseDto>

    @GET("api/chat/conversations")
    suspend fun getConversations(): Response<ChatConversationsResponseDto>

    @GET("api/chat/{id}/messages")
    suspend fun getMessages(
        @Path("id") roomId: Int,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ChatMessagesResponseDto>

    @POST("api/chat/{id}/messages")
    suspend fun sendMessage(
        @Path("id") roomId: Int,
        @Body request: SendMessageRequestDto
    ): Response<SendMessageResponseDto>

    @GET("api/admin/chat")
    suspend fun getAllChatRooms(): Response<ChatConversationsResponseDto>
}
