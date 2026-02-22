package com.example.scamazon_frontend.data.repository

import com.example.scamazon_frontend.core.network.ApiResponse
import com.example.scamazon_frontend.core.utils.Resource
import com.example.scamazon_frontend.data.models.admin.*
import com.example.scamazon_frontend.data.models.category.CategoryDto
import com.example.scamazon_frontend.data.models.order.AdminOrderListDataDto
import com.example.scamazon_frontend.data.models.order.OrderDetailDataDto
import com.example.scamazon_frontend.data.models.product.ProductPaginationResponse
import com.example.scamazon_frontend.data.remote.AdminService
import com.example.scamazon_frontend.data.remote.BrandService
import com.example.scamazon_frontend.data.remote.CategoryService
import com.example.scamazon_frontend.data.remote.ProductService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import org.json.JSONObject
import retrofit2.Response

class AdminRepository(
    private val adminService: AdminService,
    private val productService: ProductService,
    private val categoryService: CategoryService,
    private val brandService: BrandService
) {

    // ==================== Dashboard ====================

    suspend fun getDashboardStats(): Resource<DashboardStatsDto> {
        return safeApiCall { adminService.getDashboardStats() }
    }

    // ==================== Upload ====================

    suspend fun uploadImage(file: MultipartBody.Part): Resource<UploadDataDto> {
        return safeApiCall { adminService.uploadImage(file) }
    }

    // ==================== Product Read (reuse existing services) ====================

    suspend fun getProducts(
        page: Int? = null,
        limit: Int? = null,
        search: String? = null,
        categoryId: Int? = null,
        brandId: Int? = null
    ): Resource<ProductPaginationResponse> {
        return safeApiCall {
            productService.getProducts(
                page = page,
                limit = limit,
                search = search,
                categoryId = categoryId,
                brandId = brandId
            )
        }
    }

    // ==================== Product CRUD ====================

    suspend fun createProduct(request: CreateProductRequest): Resource<Any> {
        return safeApiCall { adminService.createProduct(request) }
    }

    suspend fun updateProduct(id: Int, request: UpdateProductRequest): Resource<Any> {
        return safeApiCall { adminService.updateProduct(id, request) }
    }

    suspend fun deleteProduct(id: Int): Resource<Any> {
        return safeApiCall { adminService.deleteProduct(id) }
    }

    // ==================== Category Read ====================

    suspend fun getCategories(): Resource<List<CategoryDto>> {
        return safeApiCall { categoryService.getCategories() }
    }

    // ==================== Category CRUD ====================

    suspend fun createCategory(request: CreateCategoryRequest): Resource<Any> {
        return safeApiCall { adminService.createCategory(request) }
    }

    suspend fun updateCategory(id: Int, request: UpdateCategoryRequest): Resource<Any> {
        return safeApiCall { adminService.updateCategory(id, request) }
    }

    suspend fun deleteCategory(id: Int): Resource<Any> {
        return safeApiCall { adminService.deleteCategory(id) }
    }

    // ==================== Brand Read ====================

    suspend fun getBrands(): Resource<List<BrandDto>> {
        return safeApiCall { brandService.getBrands() }
    }

    // ==================== Brand CRUD ====================

    suspend fun createBrand(request: CreateBrandRequest): Resource<Any> {
        return safeApiCall { adminService.createBrand(request) }
    }

    suspend fun updateBrand(id: Int, request: UpdateBrandRequest): Resource<Any> {
        return safeApiCall { adminService.updateBrand(id, request) }
    }

    suspend fun deleteBrand(id: Int): Resource<Any> {
        return safeApiCall { adminService.deleteBrand(id) }
    }

    // ==================== Order Management ====================

    suspend fun getAdminOrders(): Resource<AdminOrderListDataDto> {
        return safeApiCall { adminService.getAdminOrders() }
    }

    suspend fun getAdminOrderDetail(id: Int): Resource<OrderDetailDataDto> {
        return safeApiCall { adminService.getAdminOrderDetail(id) }
    }

    suspend fun updateOrderStatus(orderId: Int, status: String): Resource<Any> {
        return safeApiCall { adminService.updateOrderStatus(orderId, mapOf("status" to status)) }
    }

    // ==================== Helper ====================

    private suspend fun <T> safeApiCall(apiCall: suspend () -> Response<ApiResponse<T>>): Resource<T> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiCall()
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.success) {
                        if (body.data != null) {
                            Resource.Success(body.data)
                        } else {
                            Resource.Success(body.data as T)
                        }
                    } else {
                        Resource.Error(body?.message ?: "Unknown error")
                    }
                } else {
                    var errorMsg = "API Error: ${response.code()}"
                    response.errorBody()?.string()?.let {
                        try {
                            val json = JSONObject(it)
                            val errorsObj = json.optJSONObject("errors") ?: json.optJSONObject("Errors")
                            var specificErrorMsg: String? = null
                            if (errorsObj != null) {
                                val keys = errorsObj.keys()
                                if (keys.hasNext()) {
                                    val firstKey = keys.next()
                                    val messagesArray = errorsObj.optJSONArray(firstKey)
                                    if (messagesArray != null && messagesArray.length() > 0) {
                                        val fieldMsg = messagesArray.optString(0)
                                        if (fieldMsg.isNotEmpty()) specificErrorMsg = fieldMsg
                                    }
                                }
                            }
                            val genericMsg = json.optString("message")
                                .ifEmpty { json.optString("Message") }
                                .ifEmpty { json.optString("title") }
                            if (!specificErrorMsg.isNullOrEmpty()) {
                                errorMsg = specificErrorMsg
                            } else if (genericMsg.isNotEmpty()) {
                                errorMsg = genericMsg
                            }
                        } catch (_: Exception) {}
                    }
                    Resource.Error(errorMsg)
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Network error occurred")
            }
        }
    }
}
