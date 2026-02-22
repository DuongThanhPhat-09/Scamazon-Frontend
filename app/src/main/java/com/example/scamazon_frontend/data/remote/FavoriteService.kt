package com.example.scamazon_frontend.data.remote

import com.example.scamazon_frontend.core.network.ApiResponse
import com.example.scamazon_frontend.data.models.favorite.FavoriteItemDto
import com.example.scamazon_frontend.data.models.favorite.FavoriteToggleDataDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface FavoriteService {
    @GET("/api/favorites")
    suspend fun getFavorites(): Response<ApiResponse<List<FavoriteItemDto>>>

    @GET("/api/favorites/ids")
    suspend fun getFavoriteIds(): Response<ApiResponse<List<Int>>>

    @POST("/api/favorites/toggle/{productId}")
    suspend fun toggleFavorite(
        @Path("productId") productId: Int
    ): Response<ApiResponse<FavoriteToggleDataDto>>
}
