package com.example.scamazon_frontend.core.network

import com.google.gson.annotations.SerializedName

/**
 * Generic API Response matching the backend milestone specification.
 * { "success": true, "message": "Success", "data": { ... } }
 */
data class ApiResponse<T>(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String? = null,
    
    @SerializedName("data")
    val data: T? = null
)
