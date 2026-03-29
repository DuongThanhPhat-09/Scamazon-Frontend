package com.example.scamazon_frontend.data.repository

import com.example.scamazon_frontend.core.network.safeApiCall
import com.example.scamazon_frontend.core.utils.Resource
import com.example.scamazon_frontend.data.models.review.ReviewDto
import com.example.scamazon_frontend.data.models.review.ReviewListDataDto
import com.example.scamazon_frontend.data.models.review.ReviewRequestDto
import com.example.scamazon_frontend.data.remote.ReviewService

class ReviewRepository(private val reviewService: ReviewService) {

    suspend fun getProductReviews(
        productId: Int,
        page: Int = 1,
        limit: Int = 10,
        rating: Int? = null
    ): Resource<ReviewListDataDto> {
        return safeApiCall { reviewService.getProductReviews(productId, page, limit, rating) }
    }

    suspend fun createReview(productId: Int, rating: Int, comment: String?): Resource<ReviewDto> {
        return safeApiCall {
            reviewService.createReview(productId, ReviewRequestDto(rating, comment))
        }
    }
}
