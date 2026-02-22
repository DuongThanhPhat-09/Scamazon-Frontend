package com.example.scamazon_frontend.ui.screens.admin.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scamazon_frontend.core.utils.Resource
import com.example.scamazon_frontend.data.models.admin.*
import com.example.scamazon_frontend.data.models.category.CategoryDto
import com.example.scamazon_frontend.data.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminCategoryViewModel(
    private val repository: AdminRepository
) : ViewModel() {

    private val _categoriesState = MutableStateFlow<Resource<List<CategoryDto>>>(Resource.Loading())
    val categoriesState: StateFlow<Resource<List<CategoryDto>>> = _categoriesState.asStateFlow()

    private val _brandsState = MutableStateFlow<Resource<List<BrandDto>>>(Resource.Loading())
    val brandsState: StateFlow<Resource<List<BrandDto>>> = _brandsState.asStateFlow()

    private val _saveState = MutableStateFlow<Resource<Any>?>(null)
    val saveState: StateFlow<Resource<Any>?> = _saveState.asStateFlow()

    private val _deleteState = MutableStateFlow<Resource<Any>?>(null)
    val deleteState: StateFlow<Resource<Any>?> = _deleteState.asStateFlow()

    init {
        loadCategories()
        loadBrands()
    }

    fun loadCategories() {
        viewModelScope.launch {
            _categoriesState.value = Resource.Loading()
            _categoriesState.value = repository.getCategories()
        }
    }

    fun loadBrands() {
        viewModelScope.launch {
            _brandsState.value = Resource.Loading()
            _brandsState.value = repository.getBrands()
        }
    }

    // Category CRUD
    fun createCategory(request: CreateCategoryRequest) {
        viewModelScope.launch {
            _saveState.value = Resource.Loading()
            _saveState.value = repository.createCategory(request)
        }
    }

    fun updateCategory(id: Int, request: UpdateCategoryRequest) {
        viewModelScope.launch {
            _saveState.value = Resource.Loading()
            _saveState.value = repository.updateCategory(id, request)
        }
    }

    fun deleteCategory(id: Int) {
        viewModelScope.launch {
            _deleteState.value = Resource.Loading()
            _deleteState.value = repository.deleteCategory(id)
        }
    }

    // Brand CRUD
    fun createBrand(request: CreateBrandRequest) {
        viewModelScope.launch {
            _saveState.value = Resource.Loading()
            _saveState.value = repository.createBrand(request)
        }
    }

    fun updateBrand(id: Int, request: UpdateBrandRequest) {
        viewModelScope.launch {
            _saveState.value = Resource.Loading()
            _saveState.value = repository.updateBrand(id, request)
        }
    }

    fun deleteBrand(id: Int) {
        viewModelScope.launch {
            _deleteState.value = Resource.Loading()
            _deleteState.value = repository.deleteBrand(id)
        }
    }

    fun resetSaveState() {
        _saveState.value = null
    }

    fun resetDeleteState() {
        _deleteState.value = null
    }
}
