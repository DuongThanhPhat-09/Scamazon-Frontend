package com.example.scamazon_frontend.data.models.admin

import com.google.gson.annotations.SerializedName

data class BrandDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("slug") val slug: String,
    @SerializedName("logoUrl") val logoUrl: String?,
    @SerializedName("description") val description: String?
)
