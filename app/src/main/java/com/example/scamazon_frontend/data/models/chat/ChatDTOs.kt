package com.example.scamazon_frontend.data.models.chat

data class ChatConversationsResponseDto(
    val success: Boolean,
    val message: String,
    val data: List<ChatRoomSummaryDto>?
)

data class ChatRoomSummaryDto(
    val id: Int,
    val userId: Int,
    val userName: String?,
    val userAvatar: String?,
    val storeId: Int?,
    val storeName: String?,
    val status: String?,
    val lastMessage: String?,
    val lastMessageAt: String?,
    val unreadCount: Int,
    val createdAt: String?
)

data class ChatMessagesResponseDto(
    val success: Boolean,
    val message: String,
    val data: ChatMessagesDataDto?
)

data class ChatMessagesDataDto(
    val chatRoomId: Int,
    val messages: List<ChatMessageDto> = emptyList()
)

data class ChatMessageDto(
    val id: Int,
    val chatRoomId: Int,
    val senderId: Int?,
    val senderName: String?,
    val messageType: String?,
    val content: String,
    val imageUrl: String?,
    val productId: Int?,
    val productName: String?,
    val productImage: String?,
    val isFromStore: Boolean?,
    val isRead: Boolean?,
    val createdAt: String?
)

data class SendMessageResponseDto(
    val success: Boolean,
    val message: String,
    val data: ChatMessageDto?
)

data class StartChatResponseDto(
    val success: Boolean,
    val message: String,
    val data: ChatRoomSummaryDto?
)

data class SendMessageRequestDto(
    val content: String,
    val messageType: String = "text",
    val imageUrl: String? = null,
    val productId: Int? = null
)

data class StartChatRequestDto(
    val storeId: Int? = null
)
