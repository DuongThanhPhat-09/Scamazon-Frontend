package com.example.scamazon_frontend.ui.screens.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scamazon_frontend.core.network.SignalRManager
import com.example.scamazon_frontend.core.utils.Resource
import com.example.scamazon_frontend.data.models.notification.NotificationDto
import com.example.scamazon_frontend.data.repository.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val repository: NotificationRepository,
    private val signalRManager: SignalRManager
) : ViewModel() {

    private val _notificationsState = MutableStateFlow<Resource<List<NotificationDto>>>(Resource.Loading())
    val notificationsState = _notificationsState.asStateFlow()

    init {
        loadNotifications()
        // Here we could listen for signalRManager.notificationFlow if needed
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _notificationsState.value = Resource.Loading()
            when (val response = repository.getNotifications()) {
                is Resource.Success -> {
                    _notificationsState.value = Resource.Success(response.data?.data ?: emptyList())
                }
                is Resource.Error -> {
                    _notificationsState.value = Resource.Error(response.message ?: "Failed to load notifications")
                }
                else -> Unit
            }
        }
    }

    fun markAsRead(id: Int) {
        viewModelScope.launch {
            val response = repository.markAsRead(id)
            if (response is Resource.Success) {
                // Update local list
                val currentList = _notificationsState.value.data?.toMutableList() ?: return@launch
                val index = currentList.indexOfFirst { it.id == id }
                if (index != -1) {
                    currentList[index] = currentList[index].copy(isRead = true)
                    _notificationsState.value = Resource.Success(currentList)
                }
            }
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            val response = repository.markAllAsRead()
            if (response is Resource.Success) {
                // Update local list
                val currentList = _notificationsState.value.data?.map { it.copy(isRead = true) } ?: emptyList()
                _notificationsState.value = Resource.Success(currentList)
            }
        }
    }
}
