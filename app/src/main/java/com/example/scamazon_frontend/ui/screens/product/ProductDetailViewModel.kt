package com.example.scamazon_frontend.ui.screens.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scamazon_frontend.core.utils.CartCountManager
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

    // Lưu quantity hiện tại của product trong cart
    private val _cartQuantityForProduct = MutableStateFlow(0)
    val cartQuantityForProduct: StateFlow<Int> = _cartQuantityForProduct.asStateFlow()

    fun loadProduct(slug: String) {
        viewModelScope.launch {
            _productState.value = Resource.Loading()
            _productState.value = productRepository.getProductBySlug(slug)
        }
    }

    fun loadCartQuantityForProduct(productId: Int) {
        viewModelScope.launch {
            val result = cartRepository.getCart()
            if (result is Resource.Success) {
                val cartItem = result.data?.items?.find { it.productId == productId }
                _cartQuantityForProduct.value = cartItem?.quantity ?: 0
            }
        }
    }

    fun addToCart(productId: Int, quantity: Int = 1, stockQuantity: Int) {
        val cartQty = _cartQuantityForProduct.value
        if (stockQuantity <= 0) {
            _addToCartState.value = Resource.Error("Sản phẩm đã hết hàng")
            return
        }
        if (cartQty + quantity > stockQuantity) {
            val remaining = stockQuantity - cartQty
            _addToCartState.value = if (remaining <= 0) {
                Resource.Error("Bạn đã thêm tối đa số lượng sản phẩm còn trong kho")
            } else {
                Resource.Error("Chỉ có thể thêm tối đa $remaining sản phẩm nữa (còn $stockQuantity trong kho)")
            }
            return
        }
        viewModelScope.launch {
            _addToCartState.value = Resource.Loading()
            val result = cartRepository.addToCart(
                AddToCartRequest(productId = productId, quantity = quantity)
            )
            if (result is Resource.Success) {
                result.data?.let {
                    CartCountManager.updateCount(it.totalItems)
                    val updatedItem = it.items.find { item -> item.productId == productId }
                    _cartQuantityForProduct.value = updatedItem?.quantity ?: (cartQty + quantity)
                }
            }
            _addToCartState.value = result
        }
    }

    fun resetAddToCartState() {
        _addToCartState.value = null
    }
}
