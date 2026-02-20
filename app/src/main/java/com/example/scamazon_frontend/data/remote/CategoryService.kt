package com.example.scamazon_frontend.data.remote

import com.example.scamazon_frontend.core.network.ApiResponse
import com.example.scamazon_frontend.data.models.category.CategoryDto
import retrofit2.Response
import retrofit2.http.GET

interface CategoryService {
    @GET("/api/categories")
    suspend fun getCategories(): Response<ApiResponse<List<CategoryDto>>>
}
