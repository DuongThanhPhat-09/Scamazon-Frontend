package com.example.scamazon_frontend.ui.screens.admin.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scamazon_frontend.core.utils.Resource
import com.example.scamazon_frontend.data.models.admin.DashboardStatsDto
import com.example.scamazon_frontend.data.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import com.example.scamazon_frontend.core.network.AppEvent
import com.example.scamazon_frontend.core.network.SignalRManager

class AdminDashboardViewModel(
    private val repository: AdminRepository,
    private val signalRManager: SignalRManager
) : ViewModel() {

    private val _statsState = MutableStateFlow<Resource<DashboardStatsDto>>(Resource.Loading())
    val statsState: StateFlow<Resource<DashboardStatsDto>> = _statsState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        loadStats()

        viewModelScope.launch {
            signalRManager.events.collect { event ->
                // Both new orders and new products should update dashboard stats
                if (event == AppEvent.OrderUpdated || event == AppEvent.ProductUpdated) {
                    loadStats()
                }
            }
        }
    }

    fun loadStats() {
        viewModelScope.launch {
            _statsState.value = Resource.Loading()
            _statsState.value = repository.getDashboardStats()
        }
    }


}
