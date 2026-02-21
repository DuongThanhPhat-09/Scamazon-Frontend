package com.example.scamazon_frontend.data.models.product

import com.google.gson.annotations.SerializedName

data class ProductDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("slug") val slug: String,
    @SerializedName("description") val description: String?,
    @SerializedName("detail_description") val detailDescription: String?,
    @SerializedName("price") val price: Double,
    @SerializedName("sale_price") val salePrice: Double?,
    @SerializedName("stock_quantity") val stockQuantity: Int,
    @SerializedName("category_id") val categoryId: Int?,
    @SerializedName("brand_id") val brandId: Int?,
    @SerializedName("primary_image") val primaryImage: String?,
    @SerializedName("images") val images: List<ProductImageDto>? = null,
    @SerializedName("avg_rating") val avgRating: Float?,
    @SerializedName("sold_count") val soldCount: Int?,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("is_featured") val isFeatured: Boolean,
    @SerializedName("created_at") val createdAt: String
)

data class ProductImageDto(
    @SerializedName("id") val id: Int,
    @SerializedName("image_url") val imageUrl: String,
    @SerializedName("is_primary") val isPrimary: Boolean,
    @SerializedName("sort_order") val sortOrder: Int
)

data class ProductPaginationResponse(
    @SerializedName("products") val items: List<ProductDto>,
    @SerializedName("pagination") val pagination: PaginationMetadata
)

data class PaginationMetadata(
    @SerializedName("current_page") val currentPage: Int,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("total_items") val totalItems: Int
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

