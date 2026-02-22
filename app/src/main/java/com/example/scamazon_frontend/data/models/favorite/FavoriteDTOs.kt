package com.example.scamazon_frontend.data.models.favorite

import com.google.gson.annotations.SerializedName

// ==================== Response ====================

data class FavoriteItemDto(
    @SerializedName("id") val id: Int,
    @SerializedName("productId") val productId: Int,
    @SerializedName("productName") val productName: String,
    @SerializedName("productSlug") val productSlug: String,
    @SerializedName("productImage") val productImage: String?,
    @SerializedName("price") val price: Double,
    @SerializedName("salePrice") val salePrice: Double?,
    @SerializedName("createdAt") val createdAt: String?
)

data class FavoriteToggleDataDto(
    @SerializedName("isFavorited") val isFavorited: Boolean,
    @SerializedName("productId") val productId: Int
)
