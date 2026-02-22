package com.example.scamazon_frontend.ui.screens.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scamazon_frontend.core.utils.Resource
import com.example.scamazon_frontend.data.models.order.CreateOrderDataDto
import com.example.scamazon_frontend.data.models.order.CreateOrderRequest
import com.example.scamazon_frontend.data.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CheckoutViewModel(private val orderRepository: OrderRepository) : ViewModel() {

    // Form fields
    private val _shippingName = MutableStateFlow("")
    val shippingName: StateFlow<String> = _shippingName.asStateFlow()

    private val _shippingPhone = MutableStateFlow("")
    val shippingPhone: StateFlow<String> = _shippingPhone.asStateFlow()

    private val _shippingAddress = MutableStateFlow("")
    val shippingAddress: StateFlow<String> = _shippingAddress.asStateFlow()

    private val _shippingCity = MutableStateFlow("")
    val shippingCity: StateFlow<String> = _shippingCity.asStateFlow()

    private val _shippingDistrict = MutableStateFlow("")
    val shippingDistrict: StateFlow<String> = _shippingDistrict.asStateFlow()

    private val _shippingWard = MutableStateFlow("")
    val shippingWard: StateFlow<String> = _shippingWard.asStateFlow()

    private val _paymentMethod = MutableStateFlow("cod")
    val paymentMethod: StateFlow<String> = _paymentMethod.asStateFlow()

    private val _note = MutableStateFlow("")
    val note: StateFlow<String> = _note.asStateFlow()

    private val _orderState = MutableStateFlow<Resource<CreateOrderDataDto>?>(null)
    val orderState: StateFlow<Resource<CreateOrderDataDto>?> = _orderState.asStateFlow()

    fun onShippingNameChange(value: String) { _shippingName.value = value }
    fun onShippingPhoneChange(value: String) { _shippingPhone.value = value }
    fun onShippingAddressChange(value: String) { _shippingAddress.value = value }
    fun onShippingCityChange(value: String) { _shippingCity.value = value }
    fun onShippingDistrictChange(value: String) { _shippingDistrict.value = value }
    fun onShippingWardChange(value: String) { _shippingWard.value = value }
    fun onPaymentMethodChange(value: String) { _paymentMethod.value = value }
    fun onNoteChange(value: String) { _note.value = value }

    fun placeOrder() {
        if (_shippingName.value.isBlank() || _shippingPhone.value.isBlank() || _shippingAddress.value.isBlank()) {
            _orderState.value = Resource.Error("Please fill in all required fields")
            return
        }

        viewModelScope.launch {
            _orderState.value = Resource.Loading()
            _orderState.value = orderRepository.createOrder(
                CreateOrderRequest(
                    shippingName = _shippingName.value,
                    shippingPhone = _shippingPhone.value,
                    shippingAddress = _shippingAddress.value,
                    shippingCity = _shippingCity.value.ifBlank { null },
                    shippingDistrict = _shippingDistrict.value.ifBlank { null },
                    shippingWard = _shippingWard.value.ifBlank { null },
                    paymentMethod = _paymentMethod.value,
                    note = _note.value.ifBlank { null }
                )
            )
        }
    }

    fun resetOrderState() {
        _orderState.value = null
    }
}
