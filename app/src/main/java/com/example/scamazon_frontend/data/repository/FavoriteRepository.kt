package com.example.scamazon_frontend.data.repository

import com.example.scamazon_frontend.core.network.safeApiCall
import com.example.scamazon_frontend.core.utils.Resource
import com.example.scamazon_frontend.data.models.favorite.FavoriteItemDto
import com.example.scamazon_frontend.data.models.favorite.FavoriteToggleDataDto
import com.example.scamazon_frontend.data.remote.FavoriteService

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
}
