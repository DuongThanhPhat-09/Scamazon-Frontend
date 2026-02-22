package com.example.scamazon_frontend.ui.screens.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scamazon_frontend.core.utils.Resource
import com.example.scamazon_frontend.data.models.product.ProductDto
import com.example.scamazon_frontend.data.repository.HomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.scamazon_frontend.core.network.AppEvent
import com.example.scamazon_frontend.core.network.SignalRManager

class ProductListViewModel(
    private val repository: HomeRepository,
    private val signalRManager: SignalRManager
) : ViewModel() {

    private val _productsState = MutableStateFlow<Resource<List<ProductDto>>>(Resource.Loading())
    val productsState: StateFlow<Resource<List<ProductDto>>> = _productsState.asStateFlow()

    private val _currentSort = MutableStateFlow("newest")
    val currentSort: StateFlow<String> = _currentSort.asStateFlow()

    private val _currentPage = MutableStateFlow(1)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    private val _totalPages = MutableStateFlow(1)
    val totalPages: StateFlow<Int> = _totalPages.asStateFlow()

    private var categoryId: Int? = null

    // Raw data from API (unsorted)
    private val allProducts = mutableListOf<ProductDto>()

    init {
        viewModelScope.launch {
            signalRManager.events.collect { event ->
                if (event == AppEvent.ProductUpdated) {
                    refresh()
                }
            }
        }
    }

    fun init(categoryId: Int?) {
        this.categoryId = categoryId
        fetchProducts(reset = true)
    }

    fun setSort(sort: String) {
        if (_currentSort.value != sort) {
            _currentSort.value = sort
            // Client-side sort: just re-sort the already-loaded data
            applySortAndEmit()
        }
    }

    fun loadNextPage() {
        if (_currentPage.value < _totalPages.value) {
            _currentPage.value += 1
            fetchProducts(reset = false)
        }
    }

    fun refresh() {
        fetchProducts(reset = true)
    }

    private fun fetchProducts(reset: Boolean) {
        viewModelScope.launch {
            if (reset) {
                _currentPage.value = 1
                allProducts.clear()
                _productsState.value = Resource.Loading()
            }

            val result = repository.getProducts(
                page = _currentPage.value,
                limit = 20,
                categoryId = categoryId
            )

            when (result) {
                is Resource.Success -> {
                    val data = result.data
                    if (data != null) {
                        _totalPages.value = data.pagination.totalPages
                        allProducts.addAll(data.items)
                        applySortAndEmit()
                    } else {
                        _productsState.value = Resource.Success(emptyList())
                    }
                }
                is Resource.Error -> {
                    _productsState.value = Resource.Error(result.message ?: "Unknown error")
                }
                is Resource.Loading -> {}
            }
        }
    }

    private fun applySortAndEmit() {
        val sorted = when (_currentSort.value) {
            "price" -> allProducts.sortedBy { it.salePrice ?: it.price }
            "name" -> allProducts.sortedBy { it.name.lowercase() }
            "rating" -> allProducts.sortedByDescending { it.avgRating ?: 0f }
            else -> allProducts.sortedByDescending { it.id } // "newest"
        }
        _productsState.value = Resource.Success(sorted)
    }
}
