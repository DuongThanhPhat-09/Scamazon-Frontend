package com.example.scamazon_frontend.data.repository

import com.example.scamazon_frontend.core.utils.Resource
import com.example.scamazon_frontend.data.models.notification.BaseResponseDto
import com.example.scamazon_frontend.data.models.notification.NotificationListResponseDto
import com.example.scamazon_frontend.data.remote.NotificationService

class NotificationRepository(private val notificationService: NotificationService) {

    suspend fun getNotifications(page: Int = 1, limit: Int = 20): Resource<NotificationListResponseDto> {
        return try {
            val response = notificationService.getNotifications(page, limit)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    Resource.Success(body)
                } else {
                    Resource.Error(body?.message ?: "Failed to get notifications")
                }
            } else {
                Resource.Error("Server error: ${response.code()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }

    suspend fun markAsRead(id: Int): Resource<BaseResponseDto> {
        return try {
            val response = notificationService.markAsRead(id)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    Resource.Success(body)
                } else {
                    Resource.Error(body?.message ?: "Failed to mark as read")
                }
            } else {
                Resource.Error("Server error: ${response.code()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }

    suspend fun markAllAsRead(): Resource<BaseResponseDto> {
        return try {
            val response = notificationService.markAllAsRead()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    Resource.Success(body)
                } else {
                    Resource.Error(body?.message ?: "Failed to mark all as read")
                }
            } else {
                Resource.Error("Server error: ${response.code()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error occurred")
        }
    }
}
