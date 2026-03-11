package com.example.scamazon_frontend.core.utils

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Singleton quản lý cart count toàn app.
 * Mọi screen có thể observe cartCount để hiển thị badge.
 * Count cũng được persist vào SharedPreferences để dùng cho notification khi app đóng.
 */
object CartCountManager {
    private const val PREFS_NAME = "cart_prefs"
    private const val KEY_CART_COUNT = "cart_count"

    private val _cartCount = MutableStateFlow(0)
    val cartCount: StateFlow<Int> = _cartCount.asStateFlow()

    /** Cập nhật count từ API response (CartDataDto.totalItems) */
    fun updateCount(count: Int, context: Context? = null) {
        _cartCount.value = count
        context?.let { persistCount(it, count) }
    }

    /** Lưu count xuống SharedPreferences */
    private fun persistCount(context: Context, count: Int) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putInt(KEY_CART_COUNT, count)
            .apply()
    }

    /** Đọc count đã lưu (dùng cho notification khi app đóng) */
    fun getPersistedCount(context: Context): Int {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getInt(KEY_CART_COUNT, 0)
    }
}

