package com.example.scamazon_frontend.data.models.review

import com.google.gson.annotations.SerializedName

// ==================== Response ====================

data class ReviewDto(
    @SerializedName("id") val id: Int,
    @SerializedName("rating") val rating: Int,
    @SerializedName("comment") val comment: String?,
    @SerializedName("user") val user: ReviewUserDto,
    @SerializedName("createdAt") val createdAt: String?
)

data class ReviewUserDto(
    @SerializedName("id") val id: Int,
    @SerializedName("username") val username: String,
    @SerializedName("fullName") val fullName: String?,
    @SerializedName("avatarUrl") val avatarUrl: String?
)

data class ReviewListDataDto(
    @SerializedName("reviews") val reviews: List<ReviewDto> = emptyList(),
    @SerializedName("pagination") val pagination: ReviewPaginationDto
)

data class ReviewPaginationDto(
    @SerializedName("currentPage") val currentPage: Int,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("totalItems") val totalItems: Int,
    @SerializedName("limit") val limit: Int,
    @SerializedName("hasNext") val hasNext: Boolean,
    @SerializedName("hasPrev") val hasPrev: Boolean
)

// ==================== Request ====================

data class ReviewRequestDto(
    @SerializedName("rating") val rating: Int,
    @SerializedName("comment") val comment: String?
)
