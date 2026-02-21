package com.example.scamazon_frontend.data.repository

import com.example.scamazon_frontend.core.network.ApiResponse
import com.example.scamazon_frontend.core.utils.Resource
import com.example.scamazon_frontend.data.models.cart.AddToCartRequest
import com.example.scamazon_frontend.data.models.cart.CartDataDto
import com.example.scamazon_frontend.data.models.cart.UpdateCartItemRequest
import com.example.scamazon_frontend.data.remote.CartService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Response

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
        return safeApiCall { cartService.removeCartItem(id) }
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
