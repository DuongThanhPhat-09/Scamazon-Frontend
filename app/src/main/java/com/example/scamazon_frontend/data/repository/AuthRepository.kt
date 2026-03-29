package com.example.scamazon_frontend.data.repository

import com.example.scamazon_frontend.core.network.safeApiCall
import com.example.scamazon_frontend.core.network.safeApiCallMessage
import com.example.scamazon_frontend.core.utils.Resource
import com.example.scamazon_frontend.data.models.auth.AuthResponse
import com.example.scamazon_frontend.data.models.auth.ForgotPasswordRequest
import com.example.scamazon_frontend.data.models.auth.LoginRequest
import com.example.scamazon_frontend.data.models.auth.RegisterRequest
import com.example.scamazon_frontend.data.models.auth.ResetPasswordRequest
import com.example.scamazon_frontend.data.models.auth.VerifyOtpRequest
import com.example.scamazon_frontend.data.remote.AuthService

class AuthRepository(private val apiService: AuthService) {

    suspend fun login(request: LoginRequest): Resource<AuthResponse> {
        return safeApiCall { apiService.login(request) }
    }

    suspend fun register(request: RegisterRequest): Resource<AuthResponse> {
        return safeApiCall { apiService.register(request) }
    }

    suspend fun forgotPassword(request: ForgotPasswordRequest): Resource<String> {
        return safeApiCallMessage { apiService.forgotPassword(request) }
    }

    suspend fun verifyOtp(request: VerifyOtpRequest): Resource<String> {
        return safeApiCallMessage { apiService.verifyOtp(request) }
    }

    suspend fun resetPassword(request: ResetPasswordRequest): Resource<String> {
        return safeApiCallMessage { apiService.resetPassword(request) }
    }
}
