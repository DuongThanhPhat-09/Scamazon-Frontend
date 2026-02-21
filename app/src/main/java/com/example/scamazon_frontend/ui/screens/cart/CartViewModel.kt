package com.example.scamazon_frontend.ui.screens.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    init {
        fetchCart()
    }

    fun fetchCart() {
        viewModelScope.launch {
            _cartState.value = Resource.Loading()
            _cartState.value = repository.getCart()
        }
    }

    fun updateQuantity(itemId: Int, quantity: Int) {
        viewModelScope.launch {
            val result = repository.updateCartItem(itemId, UpdateCartItemRequest(quantity))
            when (result) {
                is Resource.Success -> {
                    // Refresh the whole cart to get updated totals
                    _cartState.value = result
                }
                is Resource.Error -> {
                    _operationMessage.value = result.message
                }
                else -> {}
            }
        }
    }

    fun removeItem(itemId: Int) {
        viewModelScope.launch {
            val result = repository.removeCartItem(itemId)
            when (result) {
                is Resource.Success -> {
                    // Refresh cart after removal
                    fetchCart()
                }
                is Resource.Error -> {
                    _operationMessage.value = result.message
                }
                else -> {}
            }
        }
    }

    fun clearMessage() {
        _operationMessage.value = null
    }
}
