package com.example.scamazon_frontend.ui.screens.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scamazon_frontend.core.utils.CartCountManager
import com.example.scamazon_frontend.core.utils.Resource
import com.example.scamazon_frontend.data.models.cart.CartDataDto
import com.example.scamazon_frontend.data.models.cart.UpdateCartItemRequest
import com.example.scamazon_frontend.data.repository.CartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CartViewModel(private val repository: CartRepository) : ViewModel() {

    private val _cartState = MutableStateFlow<Resource<CartDataDto>>(Resource.Loading())
    val cartState: StateFlow<Resource<CartDataDto>> = _cartState.asStateFlow()

    private val _operationMessage = MutableStateFlow<String?>(null)
    val operationMessage: StateFlow<String?> = _operationMessage.asStateFlow()

    // Multi-select state
    private val _selectedItems = MutableStateFlow<Set<Int>>(emptySet())
    val selectedItems: StateFlow<Set<Int>> = _selectedItems.asStateFlow()

    private val _isSelectionMode = MutableStateFlow(false)
    val isSelectionMode: StateFlow<Boolean> = _isSelectionMode.asStateFlow()

    init { fetchCart() }

    fun fetchCart() {
        viewModelScope.launch {
            _cartState.value = Resource.Loading()
            val result = repository.getCart()
            if (result is Resource.Success) {
                result.data?.let { CartCountManager.updateCount(it.totalItems) }
            }
            _cartState.value = result
        }
    }

    fun updateQuantity(itemId: Int, quantity: Int) {
        viewModelScope.launch {
            val result = repository.updateCartItem(itemId, UpdateCartItemRequest(quantity))
            when (result) {
                is Resource.Success -> {
                    result.data?.let { CartCountManager.updateCount(it.totalItems) }
                    _cartState.value = result
                }
                is Resource.Error -> _operationMessage.value = result.message
                else -> {}
            }
        }
    }

    fun removeItem(itemId: Int) {
        viewModelScope.launch {
            val result = repository.removeCartItem(itemId)
            when (result) {
                is Resource.Success -> {
                    _selectedItems.value = _selectedItems.value - itemId
                    if (_selectedItems.value.isEmpty()) _isSelectionMode.value = false
                    fetchCart()
                }
                is Resource.Error -> _operationMessage.value = result.message
                else -> {}
            }
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            val currentItems = (cartState.value as? Resource.Success)?.data?.items ?: return@launch
            currentItems.forEach { item ->
                repository.removeCartItem(item.id)
            }
            exitSelectionMode()
            CartCountManager.updateCount(0)
            fetchCart()
        }
    }

    fun removeSelectedItems() {
        val ids = _selectedItems.value.toList()
        viewModelScope.launch {
            ids.forEach { id -> repository.removeCartItem(id) }
            exitSelectionMode()
            fetchCart()
        }
    }

    // ── Selection mode ────────────────────────────────────────────────────────

    fun enterSelectionMode(itemId: Int) {
        _isSelectionMode.value = true
        _selectedItems.value = setOf(itemId)
    }

    fun toggleSelection(itemId: Int) {
        val current = _selectedItems.value
        _selectedItems.value = if (itemId in current) current - itemId else current + itemId
        if (_selectedItems.value.isEmpty()) _isSelectionMode.value = false
    }

    fun selectAll(itemIds: List<Int>) {
        _selectedItems.value = itemIds.toSet()
    }

    fun exitSelectionMode() {
        _isSelectionMode.value = false
        _selectedItems.value = emptySet()
    }

    fun clearMessage() { _operationMessage.value = null }
}
