package com.example.scamazon_frontend.di

import android.content.Context
import com.example.scamazon_frontend.core.network.RetrofitClient
import com.example.scamazon_frontend.core.network.SignalRManager
import com.example.scamazon_frontend.core.utils.TokenManager
import com.example.scamazon_frontend.data.remote.*
import com.example.scamazon_frontend.data.repository.*

/**
 * Simple Service Locator — caches all Retrofit services and repositories as singletons.
 *
 * Replaces the old pattern where ViewModelFactory re-created services/repositories
 * on every ViewModel instantiation.
 *
 * Usage:
 *   ServiceLocator.init(applicationContext)  // call once in MainActivity
 *   val repo = ServiceLocator.authRepository
 */
object ServiceLocator {

    @Volatile
    private var initialized = false
    private lateinit var appContext: Context

    /**
     * Initialize with Application context. Safe to call multiple times.
     */
    fun init(context: Context) {
        if (!initialized) {
            synchronized(this) {
                if (!initialized) {
                    appContext = context.applicationContext
                    initialized = true
                }
            }
        }
    }

    // ─── Core ────────────────────────────────────────────────────────────────

    private val retrofit by lazy { RetrofitClient.getClient(appContext) }

    val tokenManager by lazy { TokenManager(appContext) }

    val signalRManager by lazy { SignalRManager.getInstance(appContext) }

    // ─── Retrofit Services (cached singletons) ──────────────────────────────

    val authService: AuthService by lazy { retrofit.create(AuthService::class.java) }

    val productService: ProductService by lazy { retrofit.create(ProductService::class.java) }

    val categoryService: CategoryService by lazy { retrofit.create(CategoryService::class.java) }

    val brandService: BrandService by lazy { retrofit.create(BrandService::class.java) }

    val cartService: CartService by lazy { retrofit.create(CartService::class.java) }

    val orderService: OrderService by lazy { retrofit.create(OrderService::class.java) }

    val profileService: ProfileService by lazy { retrofit.create(ProfileService::class.java) }

    val reviewService: ReviewService by lazy { retrofit.create(ReviewService::class.java) }

    val favoriteService: FavoriteService by lazy { retrofit.create(FavoriteService::class.java) }

    val adminService: AdminService by lazy { retrofit.create(AdminService::class.java) }

    val paymentService: PaymentService by lazy { retrofit.create(PaymentService::class.java) }

    val chatService: ChatService by lazy { retrofit.create(ChatService::class.java) }

    val uploadService: UploadService by lazy { retrofit.create(UploadService::class.java) }

    val notificationService: NotificationService by lazy { retrofit.create(NotificationService::class.java) }

    // ─── Repositories (cached singletons) ───────────────────────────────────

    val authRepository by lazy { AuthRepository(authService) }

    val homeRepository by lazy { HomeRepository(productService, categoryService, brandService) }

    val productRepository by lazy { ProductRepository(productService) }

    val cartRepository by lazy { CartRepository(cartService) }

    val orderRepository by lazy { OrderRepository(orderService) }

    val profileRepository by lazy { ProfileRepository(profileService) }

    val reviewRepository by lazy { ReviewRepository(reviewService) }

    val favoriteRepository by lazy { FavoriteRepository(favoriteService) }

    val adminRepository by lazy { AdminRepository(adminService, productService, categoryService, brandService) }

    val chatRepository by lazy { ChatRepository(chatService, uploadService) }

    val notificationRepository by lazy { NotificationRepository(notificationService) }
}
