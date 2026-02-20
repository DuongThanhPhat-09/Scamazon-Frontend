package com.example.scamazon_frontend.data.models.auth

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("username") val username: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("password") val password: String
)

data class RegisterRequest(
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("fullName") val fullName: String?,
    @SerializedName("phone") val phone: String?
)
