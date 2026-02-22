package com.example.scamazon_frontend.ui.screens.admin.product

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scamazon_frontend.core.utils.Resource
import com.example.scamazon_frontend.data.models.admin.*
import com.example.scamazon_frontend.data.models.category.CategoryDto
import com.example.scamazon_frontend.data.models.product.ProductDetailDto
import com.example.scamazon_frontend.data.models.product.ProductDto
import com.example.scamazon_frontend.data.models.product.ProductPaginationResponse
import com.example.scamazon_frontend.data.repository.AdminRepository
import com.example.scamazon_frontend.data.remote.ProductService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class AdminProductViewModel(
    private val repository: AdminRepository,
    private val productService: ProductService
) : ViewModel() {

    private val _productsState = MutableStateFlow<Resource<ProductPaginationResponse>>(Resource.Loading())
    val productsState: StateFlow<Resource<ProductPaginationResponse>> = _productsState.asStateFlow()

    private val _productDetailState = MutableStateFlow<Resource<ProductDetailDto>?>(null)
    val productDetailState: StateFlow<Resource<ProductDetailDto>?> = _productDetailState.asStateFlow()

    private val _categoriesState = MutableStateFlow<Resource<List<CategoryDto>>>(Resource.Loading())
    val categoriesState: StateFlow<Resource<List<CategoryDto>>> = _categoriesState.asStateFlow()

    private val _brandsState = MutableStateFlow<Resource<List<BrandDto>>>(Resource.Loading())
    val brandsState: StateFlow<Resource<List<BrandDto>>> = _brandsState.asStateFlow()

    private val _saveState = MutableStateFlow<Resource<Any>?>(null)
    val saveState: StateFlow<Resource<Any>?> = _saveState.asStateFlow()

    private val _deleteState = MutableStateFlow<Resource<Any>?>(null)
    val deleteState: StateFlow<Resource<Any>?> = _deleteState.asStateFlow()

    private val _uploadState = MutableStateFlow<Resource<UploadDataDto>?>(null)
    val uploadState: StateFlow<Resource<UploadDataDto>?> = _uploadState.asStateFlow()

    private var currentPage = 1

    init {
        loadProducts()
        loadCategories()
        loadBrands()
    }

    fun loadProducts(page: Int = 1, search: String? = null) {
        currentPage = page
        viewModelScope.launch {
            _productsState.value = Resource.Loading()
            _productsState.value = repository.getProducts(page = page, limit = 20, search = search)
        }
    }

    fun loadProductDetail(slug: String) {
        viewModelScope.launch {
            _productDetailState.value = Resource.Loading()
            try {
                val response = productService.getProductBySlug(slug)
                if (response.isSuccessful && response.body()?.success == true) {
                    _productDetailState.value = Resource.Success(response.body()!!.data!!)
                } else {
                    _productDetailState.value = Resource.Error(response.body()?.message ?: "Error")
                }
            } catch (e: Exception) {
                _productDetailState.value = Resource.Error(e.message ?: "Network error")
            }
        }
    }

    fun loadCategories() {
        viewModelScope.launch {
            _categoriesState.value = repository.getCategories()
        }
    }

    fun loadBrands() {
        viewModelScope.launch {
            _brandsState.value = repository.getBrands()
        }
    }

    fun createProduct(request: CreateProductRequest) {
        viewModelScope.launch {
            _saveState.value = Resource.Loading()
            _saveState.value = repository.createProduct(request)
        }
    }

    fun updateProduct(id: Int, request: UpdateProductRequest) {
        viewModelScope.launch {
            _saveState.value = Resource.Loading()
            _saveState.value = repository.updateProduct(id, request)
        }
    }

    fun deleteProduct(id: Int) {
        viewModelScope.launch {
            _deleteState.value = Resource.Loading()
            _deleteState.value = repository.deleteProduct(id)
        }
    }

    fun uploadImage(context: Context, uri: Uri) {
        viewModelScope.launch {
            _uploadState.value = Resource.Loading()
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes() ?: byteArrayOf()
                inputStream?.close()

                val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
                val extension = when (mimeType) {
                    "image/png" -> "png"
                    "image/gif" -> "gif"
                    "image/webp" -> "webp"
                    else -> "jpg"
                }
                val fileName = "upload_${System.currentTimeMillis()}.$extension"

                val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
                val part = MultipartBody.Part.createFormData("file", fileName, requestBody)

                _uploadState.value = repository.uploadImage(part)
            } catch (e: Exception) {
                _uploadState.value = Resource.Error(e.message ?: "Upload failed")
            }
        }
    }

    fun resetSaveState() {
        _saveState.value = null
    }

    fun resetDeleteState() {
        _deleteState.value = null
    }

    fun resetUploadState() {
        _uploadState.value = null
    }
}
