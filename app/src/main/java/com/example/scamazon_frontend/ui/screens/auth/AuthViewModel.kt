package com.example.scamazon_frontend.ui.screens.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scamazon_frontend.core.utils.Resource
import com.example.scamazon_frontend.core.utils.TokenManager
import com.example.scamazon_frontend.data.models.auth.AuthResponse
import com.example.scamazon_frontend.data.models.auth.LoginRequest
import com.example.scamazon_frontend.data.models.auth.RegisterRequest
import com.example.scamazon_frontend.data.remote.AuthService
import com.example.scamazon_frontend.data.repository.AuthRepository
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel(
    private val repository: AuthRepository,
    private val tokenManager: TokenManager,
    private val authService: AuthService? = null
) : ViewModel() {

    private val _loginState = MutableStateFlow<Resource<AuthResponse>?>(null)
    val loginState: StateFlow<Resource<AuthResponse>?> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow<Resource<AuthResponse>?>(null)
    val registerState: StateFlow<Resource<AuthResponse>?> = _registerState.asStateFlow()

    fun login(request: LoginRequest) {
        viewModelScope.launch {
            _loginState.value = Resource.Loading()
            val result = repository.login(request)
            if (result is Resource.Success) {
                result.data?.let {
                    tokenManager.saveToken(it.token)
                    tokenManager.saveUserRole(it.user.role)
                    // Register FCM token after successful login
                    registerFcmToken()
                }
            }
            _loginState.value = result
        }
    }

    fun register(request: RegisterRequest) {
        viewModelScope.launch {
            _registerState.value = Resource.Loading()
            val result = repository.register(request)
            if (result is Resource.Success) {
                result.data?.let {
                    tokenManager.saveToken(it.token)
                    tokenManager.saveUserRole(it.user.role)
                    registerFcmToken()
                }
            }
            _registerState.value = result
        }
    }

    private fun registerFcmToken() {
        viewModelScope.launch {
            try {
                val fcmToken = FirebaseMessaging.getInstance().token.await()
                Log.d("AuthViewModel", "FCM Token: $fcmToken")
                authService?.saveFcmToken(
                    mapOf("token" to fcmToken, "deviceType" to "android")
                )
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Failed to register FCM token: ${e.message}")
            }
        }
    }
    
    fun resetState() {
        _loginState.value = null
        _registerState.value = null
    }
}
