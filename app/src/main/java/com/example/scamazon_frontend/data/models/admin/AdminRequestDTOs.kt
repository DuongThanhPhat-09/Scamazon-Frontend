package com.example.scamazon_frontend.data.models.admin

import com.google.gson.annotations.SerializedName

// ==================== Product Requests ====================

data class CreateProductRequest(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("detailDescription") val detailDescription: String? = null,
    @SerializedName("specifications") val specifications: String? = null,
    @SerializedName("price") val price: Double,
    @SerializedName("salePrice") val salePrice: Double? = null,
    @SerializedName("stockQuantity") val stockQuantity: Int = 0,
    @SerializedName("categoryId") val categoryId: Int? = null,
    @SerializedName("brandId") val brandId: Int? = null,
    @SerializedName("isFeatured") val isFeatured: Boolean = false,
    @SerializedName("images") val images: List<ProductImageRequest>? = null
)

data class UpdateProductRequest(
    @SerializedName("name") val name: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("detailDescription") val detailDescription: String? = null,
    @SerializedName("specifications") val specifications: String? = null,
    @SerializedName("price") val price: Double? = null,
    @SerializedName("salePrice") val salePrice: Double? = null,
    @SerializedName("stockQuantity") val stockQuantity: Int? = null,
    @SerializedName("categoryId") val categoryId: Int? = null,
    @SerializedName("brandId") val brandId: Int? = null,
    @SerializedName("isActive") val isActive: Boolean? = null,
    @SerializedName("isFeatured") val isFeatured: Boolean? = null,
    @SerializedName("images") val images: List<ProductImageRequest>? = null
)

data class ProductImageRequest(
    @SerializedName("url") val url: String,
    @SerializedName("isPrimary") val isPrimary: Boolean = false,
    @SerializedName("sortOrder") val sortOrder: Int = 0
)

// ==================== Category Requests ====================

data class CreateCategoryRequest(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("imageUrl") val imageUrl: String? = null,
    @SerializedName("parentId") val parentId: Int? = null
)

data class UpdateCategoryRequest(
    @SerializedName("name") val name: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("imageUrl") val imageUrl: String? = null,
    @SerializedName("parentId") val parentId: Int? = null,
    @SerializedName("isActive") val isActive: Boolean? = null
)

// ==================== Brand Requests ====================

data class CreateBrandRequest(
    @SerializedName("name") val name: String,
    @SerializedName("logoUrl") val logoUrl: String? = null,
    @SerializedName("description") val description: String? = null
)

data class UpdateBrandRequest(
    @SerializedName("name") val name: String? = null,
    @SerializedName("logoUrl") val logoUrl: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("isActive") val isActive: Boolean? = null
)
