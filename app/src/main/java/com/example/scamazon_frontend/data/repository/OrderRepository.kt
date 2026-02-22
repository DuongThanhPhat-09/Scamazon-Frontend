package com.example.scamazon_frontend.data.repository

import com.example.scamazon_frontend.core.network.ApiResponse
import com.example.scamazon_frontend.core.utils.Resource
import com.example.scamazon_frontend.data.models.order.CreateOrderDataDto
import com.example.scamazon_frontend.data.models.order.CreateOrderRequest
import com.example.scamazon_frontend.data.models.order.OrderDetailDataDto
import com.example.scamazon_frontend.data.models.order.OrderSummaryDto
import com.example.scamazon_frontend.data.remote.OrderService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Response

class OrderRepository(private val orderService: OrderService) {

    suspend fun createOrder(request: CreateOrderRequest): Resource<CreateOrderDataDto> {
        return safeApiCall { orderService.createOrder(request) }
    }

    suspend fun getMyOrders(): Resource<List<OrderSummaryDto>> {
        return safeApiCall { orderService.getMyOrders() }
    }

    suspend fun getOrderDetail(id: Int): Resource<OrderDetailDataDto> {
        return safeApiCall { orderService.getOrderDetail(id) }
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
