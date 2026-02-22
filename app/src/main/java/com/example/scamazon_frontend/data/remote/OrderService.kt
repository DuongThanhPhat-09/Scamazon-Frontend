package com.example.scamazon_frontend.data.remote

import com.example.scamazon_frontend.core.network.ApiResponse
import com.example.scamazon_frontend.data.models.order.CreateOrderDataDto
import com.example.scamazon_frontend.data.models.order.CreateOrderRequest
import com.example.scamazon_frontend.data.models.order.OrderDetailDataDto
import com.example.scamazon_frontend.data.models.order.OrderSummaryDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface OrderService {
    @POST("/api/orders")
    suspend fun createOrder(@Body request: CreateOrderRequest): Response<ApiResponse<CreateOrderDataDto>>

    @GET("/api/orders")
    suspend fun getMyOrders(): Response<ApiResponse<List<OrderSummaryDto>>>

    @GET("/api/orders/{id}")
    suspend fun getOrderDetail(@Path("id") id: Int): Response<ApiResponse<OrderDetailDataDto>>
}
