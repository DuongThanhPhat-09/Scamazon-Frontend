package com.example.scamazon_frontend.ui.screens.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scamazon_frontend.core.utils.Resource
import com.example.scamazon_frontend.data.models.cart.AddToCartRequest
import com.example.scamazon_frontend.data.models.cart.CartDataDto
import com.example.scamazon_frontend.data.models.product.ProductDetailDto
import com.example.scamazon_frontend.data.repository.CartRepository
import com.example.scamazon_frontend.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductDetailViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _productState = MutableStateFlow<Resource<ProductDetailDto>>(Resource.Loading())
    val productState: StateFlow<Resource<ProductDetailDto>> = _productState.asStateFlow()

    private val _addToCartState = MutableStateFlow<Resource<CartDataDto>?>(null)
    val addToCartState: StateFlow<Resource<CartDataDto>?> = _addToCartState.asStateFlow()

    fun loadProduct(slug: String) {
        viewModelScope.launch {
            _productState.value = Resource.Loading()
            _productState.value = productRepository.getProductBySlug(slug)
        }
    }

    fun addToCart(productId: Int, quantity: Int = 1) {
        viewModelScope.launch {
            _addToCartState.value = Resource.Loading()
            _addToCartState.value = cartRepository.addToCart(
                AddToCartRequest(productId = productId, quantity = quantity)
            )
        }
    }

    fun resetAddToCartState() {
        _addToCartState.value = null
    }
}
