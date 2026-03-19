package com.example.scamazon_frontend.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scamazon_frontend.core.utils.Resource
import com.example.scamazon_frontend.data.models.category.CategoryDto
import com.example.scamazon_frontend.data.models.product.ProductDto
import com.example.scamazon_frontend.data.models.product.ProductPaginationResponse
import com.example.scamazon_frontend.data.remote.CategoryService
import com.example.scamazon_frontend.data.remote.ProductService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val productService: ProductService,
    private val categoryService: CategoryService
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Expose sorted product LIST instead of raw API response
    private val _products = MutableStateFlow<Resource<List<ProductDto>>>(Resource.Loading())
    val products: StateFlow<Resource<List<ProductDto>>> = _products.asStateFlow()

    private val _sortBy = MutableStateFlow("newest")
    val sortBy: StateFlow<String> = _sortBy.asStateFlow()

    private val _categories = MutableStateFlow<List<CategoryDto>>(emptyList())
    val categories: StateFlow<List<CategoryDto>> = _categories.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow<Int?>(null)
    val selectedCategoryId: StateFlow<Int?> = _selectedCategoryId.asStateFlow()

    private var searchJob: Job? = null

    // Raw unsorted data from API
    private val rawProducts = mutableListOf<ProductDto>()

    init {
        loadCategories()
        searchProducts()
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(400) // debounce
            searchProducts()
        }
    }

    fun onSortChanged(sort: String) {
        _sortBy.value = sort
        applySortAndEmit()
    }

    fun onCategoryChanged(categoryId: Int?) {
        _selectedCategoryId.value = categoryId
        searchProducts()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            try {
                val response = categoryService.getCategories()
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.success && body.data != null) {
                        _categories.value = body.data
                    }
                }
            } catch (_: Exception) { }
        }
    }

    fun searchProducts() {
        viewModelScope.launch {
            _products.value = Resource.Loading()
            try {
                val response = productService.getProducts(
                    page = 1,
                    limit = 50,
                    search = _searchQuery.value.ifBlank { null },
                    categoryId = _selectedCategoryId.value
                )
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.success && body.data != null) {
                        rawProducts.clear()
                        rawProducts.addAll(body.data.items)
                        applySortAndEmit()
                    } else {
                        _products.value = Resource.Error(body?.message ?: "No products found")
                    }
                } else {
                    _products.value = Resource.Error("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                _products.value = Resource.Error(e.message ?: "Network error")
            }
        }
    }

    private fun applySortAndEmit() {
        val sorted = when (_sortBy.value) {
            "price_asc" -> rawProducts.sortedBy { it.salePrice ?: it.price }
            "price_desc" -> rawProducts.sortedByDescending { it.salePrice ?: it.price }
            "name" -> rawProducts.sortedBy { it.name.lowercase() }
            "rating" -> rawProducts.sortedByDescending { it.avgRating ?: 0f }
            else -> rawProducts.sortedByDescending { it.id } // "newest"
        }
        _products.value = Resource.Success(sorted)
    }
}
