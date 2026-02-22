package com.example.scamazon_frontend.data.models.product

import com.google.gson.annotations.SerializedName

data class ProductDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("slug") val slug: String,
    @SerializedName("description") val description: String?,
    @SerializedName(value = "detail_description", alternate = ["detailDescription"]) val detailDescription: String?,
    @SerializedName("price") val price: Double,
    @SerializedName(value = "sale_price", alternate = ["salePrice"]) val salePrice: Double?,
    @SerializedName(value = "stock_quantity", alternate = ["stockQuantity"]) val stockQuantity: Int,
    @SerializedName(value = "category_id", alternate = ["categoryId"]) val categoryId: Int?,
    @SerializedName(value = "brand_id", alternate = ["brandId"]) val brandId: Int?,
    @SerializedName(value = "primary_image", alternate = ["primaryImage", "PrimaryImage"]) val primaryImage: String?,
    @SerializedName("images") val images: List<ProductImageDto>? = null,
    @SerializedName(value = "avg_rating", alternate = ["avgRating"]) val avgRating: Float?,
    @SerializedName(value = "sold_count", alternate = ["soldCount"]) val soldCount: Int?,
    @SerializedName(value = "is_active", alternate = ["isActive"]) val isActive: Boolean,
    @SerializedName(value = "is_featured", alternate = ["isFeatured"]) val isFeatured: Boolean,
    @SerializedName(value = "created_at", alternate = ["createdAt"]) val createdAt: String?
)

data class ProductImageDto(
    @SerializedName("id") val id: Int,
    @SerializedName(value = "image_url", alternate = ["imageUrl", "ImageUrl"]) val imageUrl: String? = null,
    @SerializedName(value = "is_primary", alternate = ["isPrimary", "IsPrimary"]) val isPrimary: Boolean? = false,
    @SerializedName(value = "sort_order", alternate = ["sortOrder", "SortOrder"]) val sortOrder: Int? = 0
)

data class ProductPaginationResponse(
    @SerializedName(value = "products", alternate = ["data", "items"]) val items: List<ProductDto>,
    @SerializedName("pagination") val pagination: PaginationMetadata
)

data class PaginationMetadata(
    @SerializedName(value = "current_page", alternate = ["currentPage"]) val currentPage: Int,
    @SerializedName(value = "total_pages", alternate = ["totalPages"]) val totalPages: Int,
    @SerializedName(value = "total_items", alternate = ["totalItems"]) val totalItems: Int
)

// ==================== Product Detail ====================

data class ProductDetailDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("slug") val slug: String,
    @SerializedName("description") val description: String?,
    @SerializedName("detailDescription") val detailDescription: String?,
    @SerializedName("specifications") val specifications: Map<String, String>?,
    @SerializedName("price") val price: Double,
    @SerializedName("salePrice") val salePrice: Double?,
    @SerializedName("discountPercent") val discountPercent: Int?,
    @SerializedName("stockQuantity") val stockQuantity: Int?,
    @SerializedName("stockStatus") val stockStatus: String?,
    @SerializedName("category") val category: CategoryInfoDto?,
    @SerializedName("brand") val brand: BrandInfoDto?,
    @SerializedName("images") val images: List<ProductImageDto> = emptyList(),
    @SerializedName("ratingSummary") val ratingSummary: RatingSummaryDto?,
    @SerializedName("viewCount") val viewCount: Int?,
    @SerializedName("soldCount") val soldCount: Int?,
    @SerializedName("isFeatured") val isFeatured: Boolean?,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("updatedAt") val updatedAt: String?
)

data class CategoryInfoDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("slug") val slug: String
)

data class BrandInfoDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("slug") val slug: String,
    @SerializedName("logoUrl") val logoUrl: String?
)

data class RatingSummaryDto(
    @SerializedName("avgRating") val avgRating: Float,
    @SerializedName("totalReviews") val totalReviews: Int,
    @SerializedName("ratingBreakdown") val ratingBreakdown: RatingBreakdownDto?
)

data class RatingBreakdownDto(
    @SerializedName("five") val five: Int,
    @SerializedName("four") val four: Int,
    @SerializedName("three") val three: Int,
    @SerializedName("two") val two: Int,
    @SerializedName("one") val one: Int
)

