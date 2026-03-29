package com.example.scamazon_frontend.data.repository

import com.example.scamazon_frontend.core.network.safeApiCall
import com.example.scamazon_frontend.core.network.safeApiCallNullable
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
import okhttp3.MultipartBody

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

    // ==================== Product Read ====================

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
        return safeApiCallNullable { adminService.createProduct(request) }
    }

    suspend fun updateProduct(id: Int, request: UpdateProductRequest): Resource<Any> {
        return safeApiCallNullable { adminService.updateProduct(id, request) }
    }

    suspend fun deleteProduct(id: Int): Resource<Any> {
        return safeApiCallNullable { adminService.deleteProduct(id) }
    }

    // ==================== Category Read ====================

    suspend fun getCategories(): Resource<List<CategoryDto>> {
        return safeApiCall { categoryService.getCategories() }
    }

    // ==================== Category CRUD ====================

    suspend fun createCategory(request: CreateCategoryRequest): Resource<Any> {
        return safeApiCallNullable { adminService.createCategory(request) }
    }

    suspend fun updateCategory(id: Int, request: UpdateCategoryRequest): Resource<Any> {
        return safeApiCallNullable { adminService.updateCategory(id, request) }
    }

    suspend fun deleteCategory(id: Int): Resource<Any> {
        return safeApiCallNullable { adminService.deleteCategory(id) }
    }

    // ==================== Brand Read ====================

    suspend fun getBrands(): Resource<List<BrandDto>> {
        return safeApiCall { brandService.getBrands() }
    }

    // ==================== Brand CRUD ====================

    suspend fun createBrand(request: CreateBrandRequest): Resource<Any> {
        return safeApiCallNullable { adminService.createBrand(request) }
    }

    suspend fun updateBrand(id: Int, request: UpdateBrandRequest): Resource<Any> {
        return safeApiCallNullable { adminService.updateBrand(id, request) }
    }

    suspend fun deleteBrand(id: Int): Resource<Any> {
        return safeApiCallNullable { adminService.deleteBrand(id) }
    }

    // ==================== Order Management ====================

    suspend fun getAdminOrders(): Resource<AdminOrderListDataDto> {
        return safeApiCall { adminService.getAdminOrders() }
    }

    suspend fun getAdminOrderDetail(id: Int): Resource<OrderDetailDataDto> {
        return safeApiCall { adminService.getAdminOrderDetail(id) }
    }

    suspend fun updateOrderStatus(orderId: Int, status: String): Resource<Any> {
        return safeApiCallNullable { adminService.updateOrderStatus(orderId, mapOf("status" to status)) }
    }
}
