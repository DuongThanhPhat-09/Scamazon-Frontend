package com.example.scamazon_frontend.data.models.auth

import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("id") val id: Int,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String?,
    @SerializedName("full_name") val fullName: String?,
    @SerializedName("role") val role: String,
    @SerializedName("avatar_url") val avatarUrl: String?,
    @SerializedName("phone") val phone: String?,
    @SerializedName("address") val address: String?,
    @SerializedName("city") val city: String?,
    @SerializedName("district") val district: String?,
    @SerializedName("ward") val ward: String?,
    @SerializedName("created_at") val createdAt: String?
)

data class AuthResponse(
    @SerializedName("user") val user: UserDto,
    @SerializedName("token") val token: String
)
