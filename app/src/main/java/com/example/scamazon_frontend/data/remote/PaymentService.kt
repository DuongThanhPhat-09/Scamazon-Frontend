package com.example.scamazon_frontend.data.remote

import com.example.scamazon_frontend.core.network.ApiResponse
import com.example.scamazon_frontend.data.models.order.PaymentStatusDataDto
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit interface for Payment-related API calls
 *
 * Flow:
 * 1. After checkout with paymentMethod != "cod", call createPaymentQR -> get QR image URL
 * 2. Show QR to user
 * 3. Poll checkPaymentStatus every 3-5 seconds until paymentStatus == "success"
 */
interface PaymentService {

    /**
     * Tạo QR Code thanh toán (SePay VietQR)
     * Response returns QR image URL in data.paymentUrl
     */
    @POST("/api/payments/create-qr")
    suspend fun createPaymentQR(
        @Body request: Map<String, Int>
    ): Response<ApiResponse<PaymentQRDataDto>>

    /**
     * Kiểm tra trạng thái thanh toán (polling)
     * Mobile app gọi mỗi 3-5 giây sau khi hiển thị QR
     */
    @GET("/api/payments/status/{orderId}")
    suspend fun checkPaymentStatus(
        @Path("orderId") orderId: Int
    ): Response<ApiResponse<PaymentStatusDataDto>>
}

/**
 * Response data for QR creation
 */
data class PaymentQRDataDto(
    val paymentUrl: String
)
