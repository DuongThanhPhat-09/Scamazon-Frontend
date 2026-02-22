package com.example.scamazon_frontend.data.repository

import com.example.scamazon_frontend.core.network.ApiResponse
import com.example.scamazon_frontend.core.utils.Resource
import com.example.scamazon_frontend.data.models.favorite.FavoriteItemDto
import com.example.scamazon_frontend.data.models.favorite.FavoriteToggleDataDto
import com.example.scamazon_frontend.data.remote.FavoriteService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Response

class FavoriteRepository(private val favoriteService: FavoriteService) {

    suspend fun getFavorites(): Resource<List<FavoriteItemDto>> {
        return safeApiCall { favoriteService.getFavorites() }
    }

    suspend fun getFavoriteIds(): Resource<List<Int>> {
        return safeApiCall { favoriteService.getFavoriteIds() }
    }

    suspend fun toggleFavorite(productId: Int): Resource<FavoriteToggleDataDto> {
        return safeApiCall { favoriteService.toggleFavorite(productId) }
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
