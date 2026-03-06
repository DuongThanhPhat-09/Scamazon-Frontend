package com.example.scamazon_frontend.core.utils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Singleton quản lý cart count toàn app.
 * Mọi screen có thể observe cartCount để hiển thị badge.
 */
object CartCountManager {
    private val _cartCount = MutableStateFlow(0)
    val cartCount: StateFlow<Int> = _cartCount.asStateFlow()

    /** Cập nhật count từ API response (CartDataDto.totalItems) */
    fun updateCount(count: Int) {
        _cartCount.value = count
    }
}
