package com.example.scamazon_frontend.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.scamazon_frontend.core.network.RetrofitClient
import com.example.scamazon_frontend.core.utils.TokenManager
import com.example.scamazon_frontend.data.remote.AuthService
import com.example.scamazon_frontend.data.remote.CartService
import com.example.scamazon_frontend.data.remote.CategoryService
import com.example.scamazon_frontend.data.remote.ProductService
import com.example.scamazon_frontend.data.remote.ProfileService
import com.example.scamazon_frontend.data.repository.AuthRepository
import com.example.scamazon_frontend.data.repository.CartRepository
import com.example.scamazon_frontend.data.repository.HomeRepository
import com.example.scamazon_frontend.data.repository.ProductRepository
import com.example.scamazon_frontend.data.repository.ProfileRepository
import com.example.scamazon_frontend.ui.screens.auth.AuthViewModel
import com.example.scamazon_frontend.ui.screens.cart.CartViewModel
import com.example.scamazon_frontend.ui.screens.home.HomeViewModel
import com.example.scamazon_frontend.ui.screens.product.ProductListViewModel
import com.example.scamazon_frontend.ui.screens.product.ProductDetailViewModel
import com.example.scamazon_frontend.ui.screens.profile.ProfileViewModel

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

        if (modelClass.isAssignableFrom(ProductListViewModel::class.java)) {
            val productService = retrofit.create(ProductService::class.java)
            val categoryService = retrofit.create(CategoryService::class.java)
            val homeRepository = HomeRepository(productService, categoryService)
            @Suppress("UNCHECKED_CAST")
            return ProductListViewModel(homeRepository) as T
        }

        if (modelClass.isAssignableFrom(ProductDetailViewModel::class.java)) {
            val productService = retrofit.create(ProductService::class.java)
            val cartService = retrofit.create(CartService::class.java)
            val productRepository = ProductRepository(productService)
            val cartRepository = CartRepository(cartService)
            @Suppress("UNCHECKED_CAST")
            return ProductDetailViewModel(productRepository, cartRepository) as T
        }

        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            val cartService = retrofit.create(CartService::class.java)
            val cartRepository = CartRepository(cartService)
            @Suppress("UNCHECKED_CAST")
            return CartViewModel(cartRepository) as T
        }

        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            val profileService = retrofit.create(ProfileService::class.java)
            val profileRepository = ProfileRepository(profileService)
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(profileRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
