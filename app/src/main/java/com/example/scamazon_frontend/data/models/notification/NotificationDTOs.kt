package com.example.scamazon_frontend.data.models.notification

data class NotificationListResponseDto(
    val success: Boolean,
    val message: String,
    val data: List<NotificationDto>?
)

data class NotificationDto(
    val id: Int,
    val type: String,
    val title: String,
    val body: String?,
    val data: String?,
    val isRead: Boolean?,
    val createdAt: String?
)

data class BaseResponseDto(
    val success: Boolean,
    val message: String
)
