package com.example.scamazon_frontend.ui.screens.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scamazon_frontend.core.utils.Resource
import com.example.scamazon_frontend.data.models.order.OrderDetailDataDto
import com.example.scamazon_frontend.data.models.order.OrderSummaryDto
import com.example.scamazon_frontend.data.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import com.example.scamazon_frontend.core.network.AppEvent
import com.example.scamazon_frontend.core.network.SignalRManager

class OrderHistoryViewModel(
    private val repository: OrderRepository,
    private val signalRManager: SignalRManager
) : ViewModel() {

    private val _ordersState = MutableStateFlow<Resource<List<OrderSummaryDto>>>(Resource.Loading())
    val ordersState: StateFlow<Resource<List<OrderSummaryDto>>> = _ordersState.asStateFlow()

    private val _orderDetailState = MutableStateFlow<Resource<OrderDetailDataDto>?>(null)
    val orderDetailState: StateFlow<Resource<OrderDetailDataDto>?> = _orderDetailState.asStateFlow()

    init {
        fetchOrders()

        viewModelScope.launch {
            signalRManager.events.collect { event ->
                if (event == AppEvent.OrderUpdated) {
                    fetchOrders()
                }
            }
        }
    }

    fun fetchOrders() {
        viewModelScope.launch {
            _ordersState.value = Resource.Loading()
            _ordersState.value = repository.getMyOrders()
        }
    }

    fun fetchOrderDetail(id: Int) {
        viewModelScope.launch {
            _orderDetailState.value = Resource.Loading()
            _orderDetailState.value = repository.getOrderDetail(id)
        }
    }
}
