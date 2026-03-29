package com.example.scamazon_frontend.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.scamazon_frontend.ui.screens.admin.category.AdminCategoryViewModel
import com.example.scamazon_frontend.ui.screens.admin.chat.AdminChatDetailViewModel
import com.example.scamazon_frontend.ui.screens.admin.chat.AdminChatListViewModel
import com.example.scamazon_frontend.ui.screens.admin.dashboard.AdminDashboardViewModel
import com.example.scamazon_frontend.ui.screens.admin.order.AdminOrderViewModel
import com.example.scamazon_frontend.ui.screens.admin.product.AdminProductViewModel
import com.example.scamazon_frontend.ui.screens.auth.AuthViewModel
import com.example.scamazon_frontend.ui.screens.cart.CartViewModel
import com.example.scamazon_frontend.ui.screens.chat.ChatViewModel
import com.example.scamazon_frontend.ui.screens.checkout.CheckoutViewModel
import com.example.scamazon_frontend.ui.screens.checkout.PaymentQRViewModel
import com.example.scamazon_frontend.ui.screens.favorite.FavoriteViewModel
import com.example.scamazon_frontend.ui.screens.home.HomeViewModel
import com.example.scamazon_frontend.ui.screens.notification.NotificationViewModel
import com.example.scamazon_frontend.ui.screens.offer.TrendsViewModel
import com.example.scamazon_frontend.ui.screens.order.OrderHistoryViewModel
import com.example.scamazon_frontend.ui.screens.product.ProductDetailViewModel
import com.example.scamazon_frontend.ui.screens.product.ProductListViewModel
import com.example.scamazon_frontend.ui.screens.profile.ProfileViewModel
import com.example.scamazon_frontend.ui.screens.review.ReviewViewModel
import com.example.scamazon_frontend.ui.screens.search.SearchViewModel

/**
 * ViewModelFactory that delegates to [ServiceLocator] for cached service/repository instances.
 *
 * Benefits over the old implementation:
 * - Services and repositories are created once and cached (lazy singletons)
 * - No duplicate service/repository creation per ViewModel
 * - Easier to add new ViewModels (just add one mapping line)
 * - Cleaner, more maintainable code
 */
class ViewModelFactory(context: Context) : ViewModelProvider.Factory {

    init {
        // Ensure ServiceLocator is initialized (safe to call multiple times)
        ServiceLocator.init(context)
    }

    private val sl = ServiceLocator

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            // ─── Auth ────────────────────────────────────────────────────
            modelClass.isAssignableFrom(AuthViewModel::class.java) ->
                AuthViewModel(sl.authRepository, sl.tokenManager, sl.authService)

            // ─── Home & Product List ─────────────────────────────────────
            modelClass.isAssignableFrom(HomeViewModel::class.java) ->
                HomeViewModel(sl.homeRepository, sl.signalRManager)

            modelClass.isAssignableFrom(ProductListViewModel::class.java) ->
                ProductListViewModel(sl.homeRepository, sl.signalRManager)

            modelClass.isAssignableFrom(ProductDetailViewModel::class.java) ->
                ProductDetailViewModel(sl.productRepository, sl.cartRepository)

            // ─── Cart & Checkout ─────────────────────────────────────────
            modelClass.isAssignableFrom(CartViewModel::class.java) ->
                CartViewModel(sl.cartRepository)

            modelClass.isAssignableFrom(CheckoutViewModel::class.java) ->
                CheckoutViewModel(sl.orderRepository, sl.profileRepository)

            modelClass.isAssignableFrom(PaymentQRViewModel::class.java) ->
                PaymentQRViewModel(sl.paymentService)

            modelClass.isAssignableFrom(OrderHistoryViewModel::class.java) ->
                OrderHistoryViewModel(sl.orderRepository, sl.signalRManager)

            // ─── Profile & Favorites ─────────────────────────────────────
            modelClass.isAssignableFrom(ProfileViewModel::class.java) ->
                ProfileViewModel(sl.profileRepository)

            modelClass.isAssignableFrom(FavoriteViewModel::class.java) ->
                FavoriteViewModel(sl.favoriteRepository)

            modelClass.isAssignableFrom(ReviewViewModel::class.java) ->
                ReviewViewModel(sl.reviewRepository)

            // ─── Search & Trends ─────────────────────────────────────────
            modelClass.isAssignableFrom(SearchViewModel::class.java) ->
                SearchViewModel(sl.productService, sl.categoryService)

            modelClass.isAssignableFrom(TrendsViewModel::class.java) ->
                TrendsViewModel(sl.homeRepository, sl.signalRManager)

            // ─── Chat & Notifications ────────────────────────────────────
            modelClass.isAssignableFrom(ChatViewModel::class.java) ->
                ChatViewModel(sl.chatRepository, sl.signalRManager)

            modelClass.isAssignableFrom(NotificationViewModel::class.java) ->
                NotificationViewModel(sl.notificationRepository, sl.signalRManager)

            // ─── Admin ───────────────────────────────────────────────────
            modelClass.isAssignableFrom(AdminDashboardViewModel::class.java) ->
                AdminDashboardViewModel(sl.adminRepository, sl.signalRManager)

            modelClass.isAssignableFrom(AdminProductViewModel::class.java) ->
                AdminProductViewModel(sl.adminRepository, sl.productService, sl.signalRManager)

            modelClass.isAssignableFrom(AdminCategoryViewModel::class.java) ->
                AdminCategoryViewModel(sl.adminRepository)

            modelClass.isAssignableFrom(AdminOrderViewModel::class.java) ->
                AdminOrderViewModel(sl.adminRepository, sl.signalRManager)

            modelClass.isAssignableFrom(AdminChatListViewModel::class.java) ->
                AdminChatListViewModel(sl.chatRepository)

            modelClass.isAssignableFrom(AdminChatDetailViewModel::class.java) ->
                AdminChatDetailViewModel(sl.chatRepository, sl.signalRManager)

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        } as T
    }
}
