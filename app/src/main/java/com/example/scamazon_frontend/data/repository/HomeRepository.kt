package com.example.scamazon_frontend.data.repository

import com.example.scamazon_frontend.core.network.safeApiCall
import com.example.scamazon_frontend.core.utils.Resource
import com.example.scamazon_frontend.data.models.admin.BrandDto
import com.example.scamazon_frontend.data.models.category.CategoryDto
import com.example.scamazon_frontend.data.models.product.ProductPaginationResponse
import com.example.scamazon_frontend.data.remote.BrandService
import com.example.scamazon_frontend.data.remote.CategoryService
import com.example.scamazon_frontend.data.remote.ProductService

class HomeRepository(
    private val productService: ProductService,
    private val categoryService: CategoryService,
    private val brandService: BrandService? = null
) {

    suspend fun getCategories(): Resource<List<CategoryDto>> {
        return safeApiCall { categoryService.getCategories() }
    }

    suspend fun getBrands(): Resource<List<BrandDto>> {
        val service = brandService ?: return Resource.Error("Brand service not available")
        return safeApiCall { service.getBrands() }
    }

    suspend fun getProducts(
        page: Int? = null,
        limit: Int? = null,
        categoryId: Int? = null,
        brandId: Int? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        minRating: Int? = null,
        sort: String? = null,
        sortOrder: String? = null,
        isFeatured: Boolean? = null
    ): Resource<ProductPaginationResponse> {
        return safeApiCall {
            productService.getProducts(
                page = page,
                limit = limit,
                categoryId = categoryId,
                brandId = brandId,
                minPrice = minPrice,
                maxPrice = maxPrice,
                minRating = minRating,
                sortBy = sort,
                sortOrder = sortOrder,
                isFeatured = isFeatured
            )
        }
    }
}
