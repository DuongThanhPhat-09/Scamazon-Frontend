package com.example.scamazon_frontend.ui.screens.admin.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scamazon_frontend.core.utils.Resource
import com.example.scamazon_frontend.data.models.order.AdminOrderSummaryDto
import com.example.scamazon_frontend.data.models.order.OrderDetailDataDto
import com.example.scamazon_frontend.data.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import com.example.scamazon_frontend.core.network.AppEvent
import com.example.scamazon_frontend.core.network.SignalRManager

class AdminOrderViewModel(
    private val repository: AdminRepository,
    private val signalRManager: SignalRManager
) : ViewModel() {

    private val _ordersState = MutableStateFlow<Resource<List<AdminOrderSummaryDto>>>(Resource.Loading())
    val ordersState: StateFlow<Resource<List<AdminOrderSummaryDto>>> = _ordersState.asStateFlow()

    private val _orderDetailState = MutableStateFlow<Resource<OrderDetailDataDto>?>(null)
    val orderDetailState: StateFlow<Resource<OrderDetailDataDto>?> = _orderDetailState.asStateFlow()

    private val _updateStatusState = MutableStateFlow<Resource<Any>?>(null)
    val updateStatusState: StateFlow<Resource<Any>?> = _updateStatusState.asStateFlow()

    private val _selectedFilter = MutableStateFlow("all")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

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
            val result = repository.getAdminOrders()
            _ordersState.value = when (result) {
                is Resource.Success -> {
                    val orders = result.data?.orders ?: emptyList()
                    Resource.Success(orders)
                }
                is Resource.Error -> Resource.Error(result.message ?: "Error loading orders")
                is Resource.Loading -> Resource.Loading()
            }
        }
    }

    fun fetchOrderDetail(id: Int) {
        viewModelScope.launch {
            _orderDetailState.value = Resource.Loading()
            _orderDetailState.value = repository.getAdminOrderDetail(id)
        }
    }

    fun updateOrderStatus(orderId: Int, newStatus: String) {
        viewModelScope.launch {
            _updateStatusState.value = Resource.Loading()
            _updateStatusState.value = repository.updateOrderStatus(orderId, newStatus)
        }
    }

    fun onFilterChange(filter: String) {
        _selectedFilter.value = filter
    }

    fun resetUpdateState() {
        _updateStatusState.value = null
    }
}
