package com.example.scamazon_frontend.data.remote

import com.example.scamazon_frontend.core.network.ApiResponse
import com.example.scamazon_frontend.data.models.admin.BrandDto
import retrofit2.Response
import retrofit2.http.GET

interface BrandService {
    @GET("/api/brands")
    suspend fun getBrands(): Response<ApiResponse<List<BrandDto>>>
}
