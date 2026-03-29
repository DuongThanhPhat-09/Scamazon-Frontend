package com.example.scamazon_frontend.data.repository

import com.example.scamazon_frontend.core.network.safeApiCall
import com.example.scamazon_frontend.core.utils.Resource
import com.example.scamazon_frontend.data.models.order.CreateOrderDataDto
import com.example.scamazon_frontend.data.models.order.CreateOrderRequest
import com.example.scamazon_frontend.data.models.order.OrderDetailDataDto
import com.example.scamazon_frontend.data.models.order.OrderSummaryDto
import com.example.scamazon_frontend.data.remote.OrderService

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
}
