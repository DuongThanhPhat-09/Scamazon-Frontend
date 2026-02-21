package com.example.scamazon_frontend.data.remote

import com.example.scamazon_frontend.core.network.ApiResponse
import com.example.scamazon_frontend.data.models.cart.AddToCartRequest
import com.example.scamazon_frontend.data.models.cart.CartDataDto
import com.example.scamazon_frontend.data.models.cart.UpdateCartItemRequest
import retrofit2.Response
import retrofit2.http.*

interface CartService {
    @GET("/api/cart")
    suspend fun getCart(): Response<ApiResponse<CartDataDto>>

    @POST("/api/cart/items")
    suspend fun addToCart(@Body request: AddToCartRequest): Response<ApiResponse<CartDataDto>>

    @PUT("/api/cart/items/{id}")
    suspend fun updateCartItem(
        @Path("id") id: Int,
        @Body request: UpdateCartItemRequest
    ): Response<ApiResponse<CartDataDto>>

    @DELETE("/api/cart/items/{id}")
    suspend fun removeCartItem(@Path("id") id: Int): Response<ApiResponse<Any>>
}
