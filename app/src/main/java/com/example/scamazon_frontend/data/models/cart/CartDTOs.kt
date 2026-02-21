package com.example.scamazon_frontend.data.models.cart

import com.google.gson.annotations.SerializedName

data class CartDataDto(
    @SerializedName("cartId") val cartId: Int,
    @SerializedName("items") val items: List<CartItemDto>,
    @SerializedName("subtotal") val subtotal: Double,
    @SerializedName("totalItems") val totalItems: Int
)

data class CartItemDto(
    @SerializedName("id") val id: Int,
    @SerializedName("productId") val productId: Int,
    @SerializedName("productName") val productName: String,
    @SerializedName("productImage") val productImage: String?,
    @SerializedName("price") val price: Double,
    @SerializedName("salePrice") val salePrice: Double?,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("itemTotal") val itemTotal: Double,
    @SerializedName("stockQuantity") val stockQuantity: Int,
    @SerializedName("isActive") val isActive: Boolean
)

data class AddToCartRequest(
    @SerializedName("productId") val productId: Int,
    @SerializedName("quantity") val quantity: Int = 1
)

data class UpdateCartItemRequest(
    @SerializedName("quantity") val quantity: Int
)
