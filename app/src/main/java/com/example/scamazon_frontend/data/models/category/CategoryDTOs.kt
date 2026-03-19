package com.example.scamazon_frontend.data.models.category

import com.google.gson.annotations.SerializedName

data class CategoryDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("slug") val slug: String,
    @SerializedName("description") val description: String?,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("parentId") val parentId: Int?,
    @SerializedName("children") val children: List<CategoryDto>? = null
)
