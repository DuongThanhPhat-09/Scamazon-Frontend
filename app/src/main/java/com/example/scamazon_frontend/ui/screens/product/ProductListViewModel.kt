package com.example.scamazon_frontend.ui.screens.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scamazon_frontend.core.utils.Resource
import com.example.scamazon_frontend.data.models.admin.BrandDto
import com.example.scamazon_frontend.data.models.product.ProductDto
import com.example.scamazon_frontend.data.repository.HomeRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.scamazon_frontend.core.network.AppEvent
import com.example.scamazon_frontend.core.network.SignalRManager

data class ProductFilter(
    val brandId: Int? = null,
    val categoryId: Int? = null,
    val minPrice: Double? = null,
    val maxPrice: Double? = null,
    val minRating: Int? = null
) {
    val activeCount: Int get() = listOfNotNull(brandId, categoryId, minPrice, maxPrice, minRating).size
    val isEmpty: Boolean get() = activeCount == 0
}

class ProductListViewModel(
    private val repository: HomeRepository,
    private val signalRManager: SignalRManager
) : ViewModel() {

    private val _productsState = MutableStateFlow<Resource<List<ProductDto>>>(Resource.Loading())
    val productsState: StateFlow<Resource<List<ProductDto>>> = _productsState.asStateFlow()

    private val _currentSort = MutableStateFlow("newest")
    val currentSort: StateFlow<String> = _currentSort.asStateFlow()

    private val _currentFilter = MutableStateFlow(ProductFilter())
    val currentFilter: StateFlow<ProductFilter> = _currentFilter.asStateFlow()

    private val _brands = MutableStateFlow<List<BrandDto>>(emptyList())
    val brands: StateFlow<List<BrandDto>> = _brands.asStateFlow()

    private val _categories = MutableStateFlow<List<com.example.scamazon_frontend.data.models.category.CategoryDto>>(emptyList())
    val categories: StateFlow<List<com.example.scamazon_frontend.data.models.category.CategoryDto>> = _categories.asStateFlow()

    private val _currentPage = MutableStateFlow(1)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    private val _totalPages = MutableStateFlow(1)
    val totalPages: StateFlow<Int> = _totalPages.asStateFlow()

    private var categoryId: Int? = null
    private var fetchJob: Job? = null

    // Raw data from API (accumulates across pages)
    private val allProducts = mutableListOf<ProductDto>()

    init {
        viewModelScope.launch {
            signalRManager.events.collect { event ->
                if (event == AppEvent.ProductUpdated) {
                    refresh()
                }
            }
        }
        loadBrands()
        loadCategories()
    }

    fun init(categoryId: Int?) {
        this.categoryId = categoryId
        fetchProducts(reset = true)
    }

    fun setSort(sort: String) {
        if (_currentSort.value != sort) {
            _currentSort.value = sort
            applySortAndEmit()
        }
    }

    fun setFilter(filter: ProductFilter) {
        _currentFilter.value = filter
        fetchProducts(reset = true)
    }

    fun clearFilter() {
        _currentFilter.value = ProductFilter()
        fetchProducts(reset = true)
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

    private fun loadBrands() {
        viewModelScope.launch {
            when (val result = repository.getBrands()) {
                is Resource.Success -> _brands.value = result.data ?: emptyList()
                else -> {}
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            when (val result = repository.getCategories()) {
                is Resource.Success -> _categories.value = result.data ?: emptyList()
                else -> {}
            }
        }
    }

    private fun fetchProducts(reset: Boolean) {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            if (reset) {
                _currentPage.value = 1
                allProducts.clear()
                _productsState.value = Resource.Loading()
            }

            val filter = _currentFilter.value
            val effectiveCategoryId = filter.categoryId ?: categoryId
            val result = repository.getProducts(
                page = _currentPage.value,
                limit = 20,
                categoryId = effectiveCategoryId,
                brandId = filter.brandId,
                minPrice = filter.minPrice,
                maxPrice = filter.maxPrice,
                minRating = filter.minRating
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
            "price_asc" -> allProducts.sortedBy { it.salePrice ?: it.price }
            "price_desc" -> allProducts.sortedByDescending { it.salePrice ?: it.price }
            "name" -> allProducts.sortedBy { it.name.lowercase() }
            "rating" -> allProducts.sortedByDescending { it.avgRating ?: 0f }
            else -> allProducts.sortedByDescending { it.id } // "newest"
        }
        _productsState.value = Resource.Success(sorted)
    }
}
