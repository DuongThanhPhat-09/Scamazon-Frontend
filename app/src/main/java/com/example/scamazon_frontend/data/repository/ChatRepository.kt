package com.example.scamazon_frontend.data.repository

import android.content.Context
import android.net.Uri
import com.example.scamazon_frontend.core.network.ApiResponse
import com.example.scamazon_frontend.core.utils.Resource
import com.example.scamazon_frontend.data.models.chat.ChatConversationsResponseDto
import com.example.scamazon_frontend.data.models.chat.ChatMessagesResponseDto
import com.example.scamazon_frontend.data.models.chat.SendMessageRequestDto
import com.example.scamazon_frontend.data.models.chat.SendMessageResponseDto
import com.example.scamazon_frontend.data.models.chat.StartChatRequestDto
import com.example.scamazon_frontend.data.models.chat.StartChatResponseDto
import com.example.scamazon_frontend.data.remote.ChatService
import com.example.scamazon_frontend.data.remote.UploadService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class ChatRepository(
    private val chatService: ChatService,
    private val uploadService: UploadService? = null
) {

    suspend fun startChat(storeId: Int? = null): Resource<StartChatResponseDto> {
        return try {
            val response = chatService.startChat(StartChatRequestDto(storeId))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) Resource.Success(body)
                else Resource.Error(body?.message ?: "Failed to start chat")
            } else {
                Resource.Error("Server error: ${response.code()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }

    suspend fun getConversations(): Resource<ChatConversationsResponseDto> {
        return try {
            val response = chatService.getConversations()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) Resource.Success(body)
                else Resource.Error(body?.message ?: "Failed to get conversations")
            } else {
                Resource.Error("Server error: ${response.code()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }

    suspend fun getMessages(roomId: Int, page: Int = 1, limit: Int = 50): Resource<ChatMessagesResponseDto> {
        return try {
            val response = chatService.getMessages(roomId, page, limit)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) Resource.Success(body)
                else Resource.Error(body?.message ?: "Failed to get messages")
            } else {
                Resource.Error("Server error: ${response.code()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }

    suspend fun sendMessage(roomId: Int, content: String, imageUrl: String? = null): Resource<SendMessageResponseDto> {
        return try {
            val request = SendMessageRequestDto(
                content = content,
                messageType = if (imageUrl != null) "image" else "text",
                imageUrl = imageUrl
            )
            val response = chatService.sendMessage(roomId, request)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) Resource.Success(body)
                else Resource.Error(body?.message ?: "Failed to send message")
            } else {
                Resource.Error("Server error: ${response.code()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }

    suspend fun uploadImage(context: Context, uri: Uri): Resource<String> {
        val service = uploadService ?: return Resource.Error("Upload service not available")
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return Resource.Error("Cannot read file")
            val bytes = inputStream.readBytes()
            inputStream.close()

            val fileName = "chat_image_${System.currentTimeMillis()}.jpg"
            val requestBody = bytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("file", fileName, requestBody)

            val response = service.uploadImage(part)
            if (response.isSuccessful) {
                val body = response.body()
                val url = body?.data?.url
                if (body?.success == true && url != null) Resource.Success(url)
                else Resource.Error("Upload failed - no URL returned")
            } else {
                Resource.Error("Upload failed: ${response.code()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Upload error")
        }
    }

    suspend fun getAllChatRooms(): Resource<ChatConversationsResponseDto> {
        return try {
            val response = chatService.getAllChatRooms()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) Resource.Success(body)
                else Resource.Error(body?.message ?: "Failed to get chat rooms")
            } else {
                Resource.Error("Server error: ${response.code()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }
}
