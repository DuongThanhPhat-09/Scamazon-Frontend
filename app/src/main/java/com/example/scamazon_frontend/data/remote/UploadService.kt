package com.example.scamazon_frontend.data.remote

import com.example.scamazon_frontend.core.network.ApiResponse
import com.example.scamazon_frontend.data.models.admin.UploadDataDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface UploadService {
    @Multipart
    @POST("api/chat/upload")
    suspend fun uploadImage(@Part file: MultipartBody.Part): Response<ApiResponse<UploadDataDto>>
}
