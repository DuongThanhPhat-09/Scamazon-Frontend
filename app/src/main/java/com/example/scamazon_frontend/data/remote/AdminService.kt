package com.example.scamazon_frontend.data.remote

import com.example.scamazon_frontend.core.network.ApiResponse
import com.example.scamazon_frontend.data.models.admin.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface AdminService {

    // ==================== Dashboard ====================

    @GET("/api/admin/dashboard/stats")
    suspend fun getDashboardStats(): Response<ApiResponse<DashboardStatsDto>>

    // ==================== Upload ====================

    @Multipart
    @POST("/api/admin/upload")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part
    ): Response<ApiResponse<UploadDataDto>>

    // ==================== Product CRUD ====================

    @POST("/api/products")
    suspend fun createProduct(
        @Body request: CreateProductRequest
    ): Response<ApiResponse<Any>>

    @PUT("/api/products/{id}")
    suspend fun updateProduct(
        @Path("id") id: Int,
        @Body request: UpdateProductRequest
    ): Response<ApiResponse<Any>>

    @DELETE("/api/products/{id}")
    suspend fun deleteProduct(
        @Path("id") id: Int
    ): Response<ApiResponse<Any>>

    // ==================== Category CRUD ====================

    @POST("/api/categories")
    suspend fun createCategory(
        @Body request: CreateCategoryRequest
    ): Response<ApiResponse<Any>>

    @PUT("/api/categories/{id}")
    suspend fun updateCategory(
        @Path("id") id: Int,
        @Body request: UpdateCategoryRequest
    ): Response<ApiResponse<Any>>

    @DELETE("/api/categories/{id}")
    suspend fun deleteCategory(
        @Path("id") id: Int
    ): Response<ApiResponse<Any>>

    // ==================== Brand CRUD ====================

    @POST("/api/brands")
    suspend fun createBrand(
        @Body request: CreateBrandRequest
    ): Response<ApiResponse<Any>>

    @PUT("/api/brands/{id}")
    suspend fun updateBrand(
        @Path("id") id: Int,
        @Body request: UpdateBrandRequest
    ): Response<ApiResponse<Any>>

    @DELETE("/api/brands/{id}")
    suspend fun deleteBrand(
        @Path("id") id: Int
    ): Response<ApiResponse<Any>>
}
