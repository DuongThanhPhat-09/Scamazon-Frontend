package com.example.scamazon_frontend.data.models.admin

import com.google.gson.annotations.SerializedName

// ==================== Dashboard Stats ====================

data class DashboardStatsDto(
    @SerializedName("customers") val customers: CustomerStatsDto,
    @SerializedName("products") val products: ProductStatsDto,
    @SerializedName("orders") val orders: OrderStatsDto,
    @SerializedName("revenue") val revenue: RevenueStatsDto,
    @SerializedName("chats") val chats: ChatStatsDto
)

data class CustomerStatsDto(
    @SerializedName("total") val total: Int,
    @SerializedName("new7Days") val new7Days: Int
)

data class ProductStatsDto(
    @SerializedName("total") val total: Int,
    @SerializedName("lowStock") val lowStock: Int
)

data class OrderStatsDto(
    @SerializedName("pending") val pending: Int,
    @SerializedName("confirmed") val confirmed: Int,
    @SerializedName("shipping") val shipping: Int,
    @SerializedName("delivered") val delivered: Int,
    @SerializedName("today") val today: Int
)

data class RevenueStatsDto(
    @SerializedName("today") val today: Double,
    @SerializedName("week") val week: Double,
    @SerializedName("month") val month: Double
)

data class ChatStatsDto(
    @SerializedName("active") val active: Int
)

// ==================== Upload Response ====================

data class UploadDataDto(
    @SerializedName("url") val url: String,
    @SerializedName("fileName") val fileName: String
)
