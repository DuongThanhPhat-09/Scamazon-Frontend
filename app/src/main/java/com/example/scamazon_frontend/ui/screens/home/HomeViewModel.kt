package com.example.scamazon_frontend.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scamazon_frontend.core.utils.Resource
import com.example.scamazon_frontend.data.models.category.CategoryDto
import com.example.scamazon_frontend.data.models.product.ProductPaginationResponse
import com.example.scamazon_frontend.data.repository.HomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import com.example.scamazon_frontend.core.network.AppEvent
import com.example.scamazon_frontend.core.network.SignalRManager

class HomeViewModel(
    private val repository: HomeRepository,
    private val signalRManager: SignalRManager
) : ViewModel() {

    private val _categoriesState = MutableStateFlow<Resource<List<CategoryDto>>>(Resource.Loading())
    val categoriesState: StateFlow<Resource<List<CategoryDto>>> = _categoriesState.asStateFlow()

    private val _flashSaleState = MutableStateFlow<Resource<ProductPaginationResponse>>(Resource.Loading())
    val flashSaleState: StateFlow<Resource<ProductPaginationResponse>> = _flashSaleState.asStateFlow()

    private val _megaSaleState = MutableStateFlow<Resource<ProductPaginationResponse>>(Resource.Loading())
    val megaSaleState: StateFlow<Resource<ProductPaginationResponse>> = _megaSaleState.asStateFlow()
    
    private val _recommendedState = MutableStateFlow<Resource<ProductPaginationResponse>>(Resource.Loading())
    val recommendedState: StateFlow<Resource<ProductPaginationResponse>> = _recommendedState.asStateFlow()

    init {
        fetchHomeData()

        viewModelScope.launch {
            signalRManager.events.collect { event ->
                if (event == AppEvent.ProductUpdated) {
                    fetchHomeData()
                }
            }
        }
    }

    fun fetchHomeData() {
        fetchCategories()
        // Simulate Flash Sale (sort by newest)
        fetchFlashSale()
        // Simulate Mega Sale (sort by discount - just fetching random for now)
        fetchMegaSale()
        // Simulate Recommended (sort by sold_count)
        fetchRecommended()
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            _categoriesState.value = Resource.Loading()
            _categoriesState.value = repository.getCategories()
        }
    }

    private fun fetchFlashSale() {
        viewModelScope.launch {
            _flashSaleState.value = Resource.Loading()
            // using sorting from backend if available, defaults to 5 items
            _flashSaleState.value = repository.getProducts(limit = 5, sort = "newest")
        }
    }

    private fun fetchMegaSale() {
        viewModelScope.launch {
            _megaSaleState.value = Resource.Loading()
            _megaSaleState.value = repository.getProducts(limit = 5, sort = "price_asc")
        }
    }
    
    private fun fetchRecommended() {
        viewModelScope.launch {
            _recommendedState.value = Resource.Loading()
            _recommendedState.value = repository.getProducts(limit = 10, sort = "popular")
        }
    }
}
