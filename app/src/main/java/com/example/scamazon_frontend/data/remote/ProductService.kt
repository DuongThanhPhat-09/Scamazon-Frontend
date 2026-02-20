package com.example.scamazon_frontend.data.remote

import com.example.scamazon_frontend.core.network.ApiResponse
import com.example.scamazon_frontend.data.models.product.ProductPaginationResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ProductService {
    @GET("/api/products")
    suspend fun getProducts(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
        @Query("category_id") categoryId: Int? = null,
        @Query("sort") sort: String? = null
    ): Response<ApiResponse<ProductPaginationResponse>>
}
