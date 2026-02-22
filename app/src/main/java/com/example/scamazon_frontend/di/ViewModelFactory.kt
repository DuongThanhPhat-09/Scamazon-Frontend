package com.example.scamazon_frontend.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.scamazon_frontend.core.network.RetrofitClient
import com.example.scamazon_frontend.core.utils.TokenManager
import com.example.scamazon_frontend.data.remote.AdminService
import com.example.scamazon_frontend.data.remote.AuthService
import com.example.scamazon_frontend.data.remote.BrandService
import com.example.scamazon_frontend.data.remote.CartService
import com.example.scamazon_frontend.data.remote.CategoryService
import com.example.scamazon_frontend.data.remote.OrderService
import com.example.scamazon_frontend.data.remote.PaymentService
import com.example.scamazon_frontend.data.remote.ProductService
import com.example.scamazon_frontend.data.remote.ProfileService
import com.example.scamazon_frontend.data.remote.ReviewService
import com.example.scamazon_frontend.data.repository.AdminRepository
import com.example.scamazon_frontend.data.repository.AuthRepository
import com.example.scamazon_frontend.data.repository.CartRepository
import com.example.scamazon_frontend.data.repository.HomeRepository
import com.example.scamazon_frontend.data.repository.OrderRepository
import com.example.scamazon_frontend.data.repository.ProductRepository
import com.example.scamazon_frontend.data.repository.ProfileRepository
import com.example.scamazon_frontend.data.repository.ReviewRepository
import com.example.scamazon_frontend.data.remote.FavoriteService
import com.example.scamazon_frontend.data.repository.FavoriteRepository
import com.example.scamazon_frontend.ui.screens.admin.category.AdminCategoryViewModel
import com.example.scamazon_frontend.ui.screens.admin.dashboard.AdminDashboardViewModel
import com.example.scamazon_frontend.ui.screens.admin.order.AdminOrderViewModel
import com.example.scamazon_frontend.ui.screens.admin.product.AdminProductViewModel
import com.example.scamazon_frontend.ui.screens.auth.AuthViewModel
import com.example.scamazon_frontend.ui.screens.cart.CartViewModel
import com.example.scamazon_frontend.ui.screens.checkout.CheckoutViewModel
import com.example.scamazon_frontend.ui.screens.checkout.PaymentQRViewModel
import com.example.scamazon_frontend.ui.screens.favorite.FavoriteViewModel
import com.example.scamazon_frontend.ui.screens.home.HomeViewModel
import com.example.scamazon_frontend.ui.screens.order.OrderHistoryViewModel
import com.example.scamazon_frontend.ui.screens.product.ProductDetailViewModel
import com.example.scamazon_frontend.ui.screens.product.ProductListViewModel
import com.example.scamazon_frontend.ui.screens.profile.ProfileViewModel
import com.example.scamazon_frontend.ui.screens.review.ReviewViewModel
import com.example.scamazon_frontend.ui.screens.search.SearchViewModel

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val retrofit = RetrofitClient.getClient(context)
        val signalRManager = com.example.scamazon_frontend.core.network.SignalRManager.getInstance(context)

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
            return HomeViewModel(homeRepository, signalRManager) as T
        }

        if (modelClass.isAssignableFrom(ProductListViewModel::class.java)) {
            val productService = retrofit.create(ProductService::class.java)
            val categoryService = retrofit.create(CategoryService::class.java)
            val homeRepository = HomeRepository(productService, categoryService)
            @Suppress("UNCHECKED_CAST")
            return ProductListViewModel(homeRepository, signalRManager) as T
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

        // Phase 3 ViewModels
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            val productService = retrofit.create(ProductService::class.java)
            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(productService) as T
        }

        if (modelClass.isAssignableFrom(CheckoutViewModel::class.java)) {
            val orderService = retrofit.create(OrderService::class.java)
            val orderRepository = OrderRepository(orderService)
            @Suppress("UNCHECKED_CAST")
            return CheckoutViewModel(orderRepository) as T
        }

        if (modelClass.isAssignableFrom(OrderHistoryViewModel::class.java)) {
            val orderService = retrofit.create(OrderService::class.java)
            val orderRepository = OrderRepository(orderService)
            @Suppress("UNCHECKED_CAST")
            return OrderHistoryViewModel(orderRepository, signalRManager) as T
        }

        // Admin ViewModels
        if (modelClass.isAssignableFrom(AdminDashboardViewModel::class.java)) {
            val adminService = retrofit.create(AdminService::class.java)
            val productService = retrofit.create(ProductService::class.java)
            val categoryService = retrofit.create(CategoryService::class.java)
            val brandService = retrofit.create(BrandService::class.java)
            val adminRepository = AdminRepository(adminService, productService, categoryService, brandService)
            @Suppress("UNCHECKED_CAST")
            return AdminDashboardViewModel(adminRepository, signalRManager) as T
        }

        if (modelClass.isAssignableFrom(AdminProductViewModel::class.java)) {
            val adminService = retrofit.create(AdminService::class.java)
            val productService = retrofit.create(ProductService::class.java)
            val categoryService = retrofit.create(CategoryService::class.java)
            val brandService = retrofit.create(BrandService::class.java)
            val adminRepository = AdminRepository(adminService, productService, categoryService, brandService)
            @Suppress("UNCHECKED_CAST")
            return AdminProductViewModel(adminRepository, productService, signalRManager) as T
        }

        if (modelClass.isAssignableFrom(AdminCategoryViewModel::class.java)) {
            val adminService = retrofit.create(AdminService::class.java)
            val productService = retrofit.create(ProductService::class.java)
            val categoryService = retrofit.create(CategoryService::class.java)
            val brandService = retrofit.create(BrandService::class.java)
            val adminRepository = AdminRepository(adminService, productService, categoryService, brandService)
            @Suppress("UNCHECKED_CAST")
            return AdminCategoryViewModel(adminRepository) as T
        }

        if (modelClass.isAssignableFrom(AdminOrderViewModel::class.java)) {
            val adminService = retrofit.create(AdminService::class.java)
            val productService = retrofit.create(ProductService::class.java)
            val categoryService = retrofit.create(CategoryService::class.java)
            val brandService = retrofit.create(BrandService::class.java)
            val adminRepository = AdminRepository(adminService, productService, categoryService, brandService)
            @Suppress("UNCHECKED_CAST")
            return AdminOrderViewModel(adminRepository, signalRManager) as T
        }

        if (modelClass.isAssignableFrom(PaymentQRViewModel::class.java)) {
            val paymentService = retrofit.create(PaymentService::class.java)
            @Suppress("UNCHECKED_CAST")
            return PaymentQRViewModel(paymentService) as T
        }

        if (modelClass.isAssignableFrom(ReviewViewModel::class.java)) {
            val reviewService = retrofit.create(ReviewService::class.java)
            val reviewRepository = ReviewRepository(reviewService)
            @Suppress("UNCHECKED_CAST")
            return ReviewViewModel(reviewRepository) as T
        }

        if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
            val favoriteService = retrofit.create(FavoriteService::class.java)
            val favoriteRepository = FavoriteRepository(favoriteService)
            @Suppress("UNCHECKED_CAST")
            return FavoriteViewModel(favoriteRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
