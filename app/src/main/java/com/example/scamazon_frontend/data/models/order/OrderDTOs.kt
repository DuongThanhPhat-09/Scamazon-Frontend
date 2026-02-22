package com.example.scamazon_frontend.data.models.order

import com.google.gson.annotations.SerializedName

// ==================== Request ====================

data class CreateOrderRequest(
    @SerializedName("shippingName") val shippingName: String,
    @SerializedName("shippingPhone") val shippingPhone: String,
    @SerializedName("shippingAddress") val shippingAddress: String,
    @SerializedName("shippingCity") val shippingCity: String? = null,
    @SerializedName("shippingDistrict") val shippingDistrict: String? = null,
    @SerializedName("shippingWard") val shippingWard: String? = null,
    @SerializedName("paymentMethod") val paymentMethod: String = "cod",
    @SerializedName("note") val note: String? = null
)

// ==================== Response ====================

data class CreateOrderDataDto(
    @SerializedName("orderId") val orderId: Int,
    @SerializedName("orderCode") val orderCode: String,
    @SerializedName("total") val total: Double,
    @SerializedName("paymentMethod") val paymentMethod: String,
    @SerializedName("paymentUrl") val paymentUrl: String?
)

data class OrderSummaryDto(
    @SerializedName("id") val id: Int,
    @SerializedName("orderCode") val orderCode: String,
    @SerializedName("total") val total: Double,
    @SerializedName("status") val status: String?,
    @SerializedName("itemCount") val itemCount: Int,
    @SerializedName("firstProductImage") val firstProductImage: String?,
    @SerializedName("createdAt") val createdAt: String?
)

data class OrderDetailDataDto(
    @SerializedName("id") val id: Int,
    @SerializedName("orderCode") val orderCode: String,
    @SerializedName("shippingName") val shippingName: String,
    @SerializedName("shippingPhone") val shippingPhone: String,
    @SerializedName("shippingAddress") val shippingAddress: String,
    @SerializedName("shippingCity") val shippingCity: String?,
    @SerializedName("shippingDistrict") val shippingDistrict: String?,
    @SerializedName("shippingWard") val shippingWard: String?,
    @SerializedName("subtotal") val subtotal: Double,
    @SerializedName("shippingFee") val shippingFee: Double,
    @SerializedName("discount") val discount: Double,
    @SerializedName("total") val total: Double,
    @SerializedName("status") val status: String?,
    @SerializedName("note") val note: String?,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("items") val items: List<OrderItemDto> = emptyList(),
    @SerializedName("payment") val payment: PaymentInfoDto?
)

data class OrderItemDto(
    @SerializedName("id") val id: Int,
    @SerializedName("productId") val productId: Int,
    @SerializedName("productName") val productName: String,
    @SerializedName("productImage") val productImage: String?,
    @SerializedName("price") val price: Double,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("subtotal") val subtotal: Double
)

data class PaymentInfoDto(
    @SerializedName("paymentMethod") val paymentMethod: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("status") val status: String?,
    @SerializedName("transactionId") val transactionId: String?,
    @SerializedName("paidAt") val paidAt: String?
)
