package com.example.scamazon_frontend.data.remote

import com.example.scamazon_frontend.core.network.ApiResponse
import com.example.scamazon_frontend.data.models.profile.ProfileDataDto
import retrofit2.Response
import retrofit2.http.GET

interface ProfileService {
    @GET("/api/auth/profile")
    suspend fun getProfile(): Response<ApiResponse<ProfileDataDto>>
}
