package com.example.scamazon_frontend.data.repository

import com.example.scamazon_frontend.core.network.safeApiCall
import com.example.scamazon_frontend.core.utils.Resource
import com.example.scamazon_frontend.data.models.cart.AddToCartRequest
import com.example.scamazon_frontend.data.models.cart.CartDataDto
import com.example.scamazon_frontend.data.models.cart.UpdateCartItemRequest
import com.example.scamazon_frontend.data.remote.CartService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CartRepository(private val cartService: CartService) {

    suspend fun getCart(): Resource<CartDataDto> {
        return safeApiCall { cartService.getCart() }
    }

    suspend fun addToCart(request: AddToCartRequest): Resource<CartDataDto> {
        return safeApiCall { cartService.addToCart(request) }
    }

    suspend fun updateCartItem(id: Int, request: UpdateCartItemRequest): Resource<CartDataDto> {
        return safeApiCall { cartService.updateCartItem(id, request) }
    }

    suspend fun removeCartItem(id: Int): Resource<Any> {
        return withContext(Dispatchers.IO) {
            try {
                val response = cartService.removeCartItem(id)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.success) {
                        Resource.Success(body.data ?: Unit)
                    } else {
                        Resource.Error(body?.message ?: "Unknown error")
                    }
                } else {
                    Resource.Error("API Error: ${response.code()}")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Network error occurred")
            }
        }
    }

    suspend fun clearCart(): Resource<Any> {
        return withContext(Dispatchers.IO) {
            try {
                val response = cartService.clearCart()
                if (response.isSuccessful) Resource.Success(Unit)
                else Resource.Error("API Error: ${response.code()}")
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Network error")
            }
        }
    }
}
