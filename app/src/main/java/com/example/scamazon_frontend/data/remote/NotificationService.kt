package com.example.scamazon_frontend.data.remote

import com.example.scamazon_frontend.data.models.notification.BaseResponseDto
import com.example.scamazon_frontend.data.models.notification.NotificationListResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface NotificationService {
    @GET("api/notifications")
    suspend fun getNotifications(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<NotificationListResponseDto>

    @PUT("api/notifications/{id}/read")
    suspend fun markAsRead(@Path("id") id: Int): Response<BaseResponseDto>

    @PUT("api/notifications/read-all")
    suspend fun markAllAsRead(): Response<BaseResponseDto>
}
