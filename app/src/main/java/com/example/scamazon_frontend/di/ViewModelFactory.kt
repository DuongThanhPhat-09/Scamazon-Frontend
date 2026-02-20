package com.example.scamazon_frontend.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.scamazon_frontend.core.network.RetrofitClient
import com.example.scamazon_frontend.core.utils.TokenManager
import com.example.scamazon_frontend.data.remote.AuthService
import com.example.scamazon_frontend.data.remote.CategoryService
import com.example.scamazon_frontend.data.remote.ProductService
import com.example.scamazon_frontend.data.repository.AuthRepository
import com.example.scamazon_frontend.data.repository.HomeRepository
import com.example.scamazon_frontend.ui.screens.auth.AuthViewModel
import com.example.scamazon_frontend.ui.screens.home.HomeViewModel

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val retrofit = RetrofitClient.getClient(context)

        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            val authService = retrofit.create(AuthService::class.java)
            val authRepository = AuthRepository(authService)
            val tokenManager = TokenManager(context)
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(authRepository, tokenManager) as T
        }

        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            val productService = retrofit.create(ProductService::class.java)
            val categoryService = retrofit.create(CategoryService::class.java)
            val homeRepository = HomeRepository(productService, categoryService)
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(homeRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
