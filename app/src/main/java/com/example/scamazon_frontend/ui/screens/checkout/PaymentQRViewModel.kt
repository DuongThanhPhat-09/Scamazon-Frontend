package com.example.scamazon_frontend.ui.screens.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scamazon_frontend.core.utils.Resource
import com.example.scamazon_frontend.data.models.order.PaymentStatusDataDto
import com.example.scamazon_frontend.data.remote.PaymentQRDataDto
import com.example.scamazon_frontend.data.remote.PaymentService
import com.example.scamazon_frontend.core.network.ApiResponse
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class PaymentQRViewModel(private val paymentService: PaymentService) : ViewModel() {

    // QR URL state
    private val _qrState = MutableStateFlow<Resource<String>>(Resource.Loading())
    val qrState: StateFlow<Resource<String>> = _qrState.asStateFlow()

    // Payment status polling
    private val _paymentStatus = MutableStateFlow("pending")
    val paymentStatus: StateFlow<String> = _paymentStatus.asStateFlow()

    private var pollingJob: Job? = null

    fun createPaymentQR(orderId: Int) {
        viewModelScope.launch {
            _qrState.value = Resource.Loading()
            try {
                val response = paymentService.createPaymentQR(mapOf("orderId" to orderId))
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success == true && body.data != null) {
                        _qrState.value = Resource.Success(body.data!!.paymentUrl)
                        // Start polling once QR is generated
                        startPolling(orderId)
                    } else {
                        _qrState.value = Resource.Error(body?.message ?: "Failed to create QR code")
                    }
                } else {
                    _qrState.value = Resource.Error("Server error: ${response.code()}")
                }
            } catch (e: Exception) {
                _qrState.value = Resource.Error(e.message ?: "Network error")
            }
        }
    }

    private fun startPolling(orderId: Int) {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (true) {
                delay(3000) // Poll every 3 seconds
                try {
                    val response = paymentService.checkPaymentStatus(orderId)
                    if (response.isSuccessful) {
                        val body = response.body()
                        val status = body?.data?.paymentStatus ?: "pending"
                        _paymentStatus.value = status
                        if (status == "success") {
                            break // Stop polling
                        }
                    }
                } catch (_: Exception) {
                    // Silently continue polling
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        pollingJob?.cancel()
    }
}
