package com.example.scamazon_frontend.data.remote

import com.example.scamazon_frontend.core.network.ApiResponse
import com.example.scamazon_frontend.data.models.review.ReviewDto
import com.example.scamazon_frontend.data.models.review.ReviewListDataDto
import com.example.scamazon_frontend.data.models.review.ReviewRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ReviewService {
    @GET("/api/products/{id}/reviews")
    suspend fun getProductReviews(
        @Path("id") id: Int,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10,
        @Query("rating") rating: Int? = null
    ): Response<ApiResponse<ReviewListDataDto>>

    @POST("/api/products/{id}/reviews")
    suspend fun createReview(
        @Path("id") id: Int,
        @Body request: ReviewRequestDto
    ): Response<ApiResponse<ReviewDto>>
}
