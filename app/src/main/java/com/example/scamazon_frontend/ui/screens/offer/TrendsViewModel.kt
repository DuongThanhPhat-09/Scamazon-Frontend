package com.example.scamazon_frontend.ui.screens.offer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scamazon_frontend.core.network.AppEvent
import com.example.scamazon_frontend.core.network.SignalRManager
import com.example.scamazon_frontend.core.utils.Resource
import com.example.scamazon_frontend.data.models.product.ProductDto
import com.example.scamazon_frontend.data.repository.HomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class TrendsFilter(val label: String) {
    ALL("All"),
    FEATURED("Featured"),
    MOST_SOLD("Most Sold"),
    NEWEST("Newest")
}

class TrendsViewModel(
    private val repository: HomeRepository,
    private val signalRManager: SignalRManager
) : ViewModel() {

    private val _productsState = MutableStateFlow<Resource<List<ProductDto>>>(Resource.Loading())
    val productsState: StateFlow<Resource<List<ProductDto>>> = _productsState.asStateFlow()

    private val _activeFilter = MutableStateFlow(TrendsFilter.ALL)
    val activeFilter: StateFlow<TrendsFilter> = _activeFilter.asStateFlow()

    private val _currentPage = MutableStateFlow(1)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    private val _totalPages = MutableStateFlow(1)
    val totalPages: StateFlow<Int> = _totalPages.asStateFlow()

    private val allProducts = mutableListOf<ProductDto>()

    init {
        fetchProducts(reset = true)
        viewModelScope.launch {
            signalRManager.events.collect { event ->
                if (event == AppEvent.ProductUpdated) fetchProducts(reset = true)
            }
        }
    }

    fun setFilter(filter: TrendsFilter) {
        if (_activeFilter.value != filter) {
            _activeFilter.value = filter
            fetchProducts(reset = true)
        }
    }

    fun loadNextPage() {
        if (_currentPage.value < _totalPages.value) {
            _currentPage.value += 1
            fetchProducts(reset = false)
        }
    }

    fun refresh() = fetchProducts(reset = true)

    private fun fetchProducts(reset: Boolean) {
        viewModelScope.launch {
            if (reset) {
                _currentPage.value = 1
                allProducts.clear()
                _productsState.value = Resource.Loading()
            }

            val (sort, sortOrder, isFeatured) = when (_activeFilter.value) {
                TrendsFilter.FEATURED  -> Triple("sold_count", "desc", true)
                TrendsFilter.MOST_SOLD -> Triple("sold_count", "desc", null)
                TrendsFilter.NEWEST    -> Triple("created_at", "desc", null)
                TrendsFilter.ALL       -> Triple("sold_count", "desc", null)
            }

            val result = repository.getProducts(
                page = _currentPage.value,
                limit = 20,
                sort = sort,
                sortOrder = sortOrder,
                isFeatured = isFeatured
            )

            when (result) {
                is Resource.Success -> {
                    val data = result.data
                    if (data != null) {
                        _totalPages.value = data.pagination.totalPages
                        allProducts.addAll(data.items)
                        _productsState.value = Resource.Success(allProducts.toList())
                    } else {
                        _productsState.value = Resource.Success(emptyList())
                    }
                }
                is Resource.Error -> _productsState.value = Resource.Error(result.message ?: "Unknown error")
                is Resource.Loading -> {}
            }
        }
    }
}
