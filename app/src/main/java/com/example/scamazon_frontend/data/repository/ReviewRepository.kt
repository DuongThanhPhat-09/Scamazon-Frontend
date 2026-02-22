package com.example.scamazon_frontend.data.repository

import com.example.scamazon_frontend.core.network.ApiResponse
import com.example.scamazon_frontend.core.utils.Resource
import com.example.scamazon_frontend.data.models.review.ReviewDto
import com.example.scamazon_frontend.data.models.review.ReviewListDataDto
import com.example.scamazon_frontend.data.remote.ReviewService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Response

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
            reviewService.createReview(
                productId,
                com.example.scamazon_frontend.data.models.review.ReviewRequestDto(rating, comment)
            )
        }
    }

    private suspend fun <T> safeApiCall(apiCall: suspend () -> Response<ApiResponse<T>>): Resource<T> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiCall()
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.success) {
                        if (body.data != null) {
                            Resource.Success(body.data)
                        } else {
                            Resource.Error(body.message ?: "No data found")
                        }
                    } else {
                        Resource.Error(body?.message ?: "Unknown error")
                    }
                } else {
                    var errorMsg = "API Error: ${response.code()}"
                    response.errorBody()?.string()?.let {
                        try {
                            val json = JSONObject(it)
                            val genericMsg = json.optString("message")
                                .ifEmpty { json.optString("Message") }
                                .ifEmpty { json.optString("title") }
                            if (genericMsg.isNotEmpty()) errorMsg = genericMsg
                        } catch (e: Exception) { /* fallback */ }
                    }
                    Resource.Error(errorMsg)
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Network error occurred")
            }
        }
    }
}
