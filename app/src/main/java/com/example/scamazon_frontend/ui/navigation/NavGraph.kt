package com.example.scamazon_frontend.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.scamazon_frontend.ui.screens.admin.account.AdminAccountScreen
import com.example.scamazon_frontend.ui.screens.admin.category.AdminCategoryFormScreen
import com.example.scamazon_frontend.ui.screens.admin.category.AdminCategoryListScreen
import com.example.scamazon_frontend.ui.screens.admin.dashboard.AdminDashboardScreen
import com.example.scamazon_frontend.ui.screens.admin.order.AdminOrderDetailScreen
import com.example.scamazon_frontend.ui.screens.admin.order.AdminOrderListScreen
import com.example.scamazon_frontend.ui.screens.admin.product.AdminProductFormScreen
import com.example.scamazon_frontend.ui.screens.admin.product.AdminProductListScreen
import com.example.scamazon_frontend.ui.screens.auth.LoginScreen
import com.example.scamazon_frontend.ui.screens.auth.RegisterScreen
import com.example.scamazon_frontend.ui.screens.cart.CartScreen
import com.example.scamazon_frontend.ui.screens.checkout.CheckoutScreen
import com.example.scamazon_frontend.ui.screens.checkout.OrderSuccessScreen
import com.example.scamazon_frontend.ui.screens.checkout.PaymentQRScreen
import com.example.scamazon_frontend.ui.screens.home.HomeScreen
import com.example.scamazon_frontend.ui.screens.order.OrderDetailScreen
import com.example.scamazon_frontend.ui.screens.order.OrderHistoryScreen
import com.example.scamazon_frontend.ui.screens.product.ProductDetailScreen
import com.example.scamazon_frontend.ui.screens.product.ProductListScreen
import com.example.scamazon_frontend.ui.screens.profile.AccountScreen
import com.example.scamazon_frontend.ui.screens.profile.EditProfileScreen
import com.example.scamazon_frontend.ui.screens.review.ReviewScreen
import com.example.scamazon_frontend.ui.screens.favorite.FavoriteScreen
import com.example.scamazon_frontend.ui.screens.search.ExploreScreen
import com.example.scamazon_frontend.ui.theme.TextSecondary
import com.example.scamazon_frontend.ui.theme.Typography

/**
 * Main Navigation Graph
 * Defines all navigation routes and their composables
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Login.route,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // ==========================================
        // AUTH SCREENS
        // ==========================================
        composable(route = Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToAdminDashboard = {
                    navController.navigate(Screen.AdminDashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Screen.ForgotPassword.route)
                }
            )
        }

        composable(route = Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // ==========================================
        // MAIN SCREENS (Bottom Navigation)
        // ==========================================
        composable(route = Screen.Home.route) {
            HomeScreen(
                onNavigateToProductDetail = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                },
                onNavigateToSearch = {
                    navController.navigate(Screen.Search.route)
                },
                onNavigateToNotifications = {
                    navController.navigate(Screen.Notifications.route)
                },
                onNavigateToWishlist = {
                    navController.navigate(Screen.Wishlist.route)
                },
                onNavigateToMap = {
                    navController.navigate(Screen.Map.route)
                },
                onNavigateToChat = {
                    navController.navigate(Screen.Chat.route)
                }
            )
        }

        composable(route = Screen.Cart.route) {
            CartScreen(
                onNavigateToCheckout = {
                    navController.navigate(Screen.Checkout.route)
                },
                onNavigateToProductDetail = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = Screen.Account.route) {
            AccountScreen(
                onNavigateToProfile = {
                    navController.navigate(Screen.EditProfile.route)
                },
                onNavigateToOrders = {
                    navController.navigate(Screen.OrderHistory.route)
                },
                onNavigateToWishlist = {
                    navController.navigate(Screen.Wishlist.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToChat = {
                    navController.navigate(Screen.Chat.route)
                },
                onNavigateToMap = {
                    navController.navigate(Screen.Map.route)
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ==========================================
        // PRODUCT SCREENS
        // ==========================================
        composable(
            route = Screen.ProductDetail.route,
            arguments = listOf(
                navArgument(NavArgs.PRODUCT_ID) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString(NavArgs.PRODUCT_ID) ?: ""
            ProductDetailScreen(
                productId = productId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToCart = {
                    navController.navigate(Screen.Cart.route)
                },
                onNavigateToReview = { id ->
                    navController.navigate(Screen.Review.createRoute(id, canWrite = false))
                }
            )
        }

        composable(
            route = Screen.ProductList.route,
            arguments = listOf(
                navArgument(NavArgs.CATEGORY_ID) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val catId = backStackEntry.arguments?.getString(NavArgs.CATEGORY_ID) ?: ""
            ProductListScreen(
                categoryId = catId,
                categoryName = null,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToProductDetail = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                }
            )
        }

        // ==========================================
        // SEARCH & EXPLORE
        // ==========================================
        composable(route = Screen.Explore.route) {
            ExploreScreen(
                onProductClick = { slug ->
                    navController.navigate(Screen.ProductDetail.createRoute(slug))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = Screen.Offer.route) {
            PlaceholderScreen(screenName = "Offers")
        }

        composable(route = Screen.Search.route) {
            ExploreScreen(
                onProductClick = { slug ->
                    navController.navigate(Screen.ProductDetail.createRoute(slug))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.SearchResult.route,
            arguments = listOf(
                navArgument(NavArgs.QUERY) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val query = backStackEntry.arguments?.getString(NavArgs.QUERY) ?: ""
            PlaceholderScreen(screenName = "Results: \"$query\"")
        }

        // ==========================================
        // CHECKOUT & PAYMENT FLOW
        // ==========================================
        composable(route = Screen.Checkout.route) {
            CheckoutScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onOrderSuccess = { orderId, orderCode, total, paymentMethod ->
                    if (paymentMethod == "vnpay") {
                        // Navigate to Payment QR screen for bank transfer
                        navController.navigate(Screen.PaymentQR.createRoute(orderId)) {
                            popUpTo(Screen.Cart.route) { inclusive = true }
                        }
                    } else {
                        // COD: go directly to success
                        navController.navigate(
                            Screen.OrderSuccess.createRoute(orderId, orderCode, total, paymentMethod)
                        ) {
                            popUpTo(Screen.Cart.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(
            route = Screen.PaymentQR.route,
            arguments = listOf(
                navArgument(NavArgs.ORDER_ID) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString(NavArgs.ORDER_ID)?.toIntOrNull() ?: 0
            PaymentQRScreen(
                orderId = orderId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onPaymentSuccess = {
                    navController.navigate(
                        Screen.OrderSuccess.createRoute(orderId.toString())
                    ) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }

        composable(
            route = Screen.OrderSuccess.route,
            arguments = listOf(
                navArgument(NavArgs.ORDER_ID) { type = NavType.StringType },
                navArgument("orderCode") { type = NavType.StringType; defaultValue = "" },
                navArgument("total") { type = NavType.StringType; defaultValue = "0" },
                navArgument("paymentMethod") { type = NavType.StringType; defaultValue = "cod" }
            )
        ) { backStackEntry ->
            val orderCode = backStackEntry.arguments?.getString("orderCode") ?: ""
            val total = backStackEntry.arguments?.getString("total")?.toDoubleOrNull() ?: 0.0
            val paymentMethod = backStackEntry.arguments?.getString("paymentMethod") ?: "cod"

            OrderSuccessScreen(
                orderCode = orderCode,
                totalAmount = total,
                paymentMethod = paymentMethod,
                onViewOrder = {
                    navController.navigate(Screen.OrderHistory.route) {
                        popUpTo(Screen.Home.route)
                    }
                },
                onContinueShopping = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        // ==========================================
        // ACCOUNT & PROFILE SCREENS
        // ==========================================
        composable(route = Screen.Profile.route) {
            EditProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = Screen.EditProfile.route) {
            EditProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // ==========================================
        // ORDER SCREENS
        // ==========================================
        composable(route = Screen.OrderHistory.route) {
            OrderHistoryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onOrderClick = { orderId ->
                    navController.navigate(Screen.OrderDetail.createRoute(orderId))
                }
            )
        }

        composable(
            route = Screen.OrderDetail.route,
            arguments = listOf(
                navArgument(NavArgs.ORDER_ID) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString(NavArgs.ORDER_ID)?.toIntOrNull() ?: 0
            OrderDetailScreen(
                orderId = orderId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToReview = { productId ->
                    navController.navigate(Screen.Review.createRoute(productId, canWrite = true))
                }
            )
        }

        composable(route = Screen.Wishlist.route) {
            FavoriteScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToProductDetail = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                }
            )
        }

        composable(route = Screen.Notifications.route) {
            val context = LocalContext.current
            val viewModel: com.example.scamazon_frontend.ui.screens.notification.NotificationViewModel = viewModel(factory = com.example.scamazon_frontend.di.ViewModelFactory(context))
            com.example.scamazon_frontend.ui.screens.notification.NotificationScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(route = Screen.Settings.route) {
            PlaceholderScreen(screenName = "Settings")
        }

        composable(route = Screen.Map.route) {
            com.example.scamazon_frontend.ui.screens.map.MapScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(route = Screen.Chat.route) {
            val context = LocalContext.current
            val viewModel: com.example.scamazon_frontend.ui.screens.chat.ChatViewModel = viewModel(factory = com.example.scamazon_frontend.di.ViewModelFactory(context))
            com.example.scamazon_frontend.ui.screens.chat.ChatScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(route = Screen.ForgotPassword.route) {
            PlaceholderScreen(screenName = "Forgot Password")
        }

        composable(
            route = Screen.Review.route,
            arguments = listOf(
                navArgument(NavArgs.PRODUCT_ID) { type = NavType.StringType },
                navArgument("canWrite") { type = NavType.StringType; defaultValue = "false" }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString(NavArgs.PRODUCT_ID)?.toIntOrNull() ?: 0
            val canWrite = backStackEntry.arguments?.getString("canWrite")?.toBoolean() ?: false
            ReviewScreen(
                productId = productId,
                canWrite = canWrite,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // ==========================================
        // ADMIN SCREENS
        // ==========================================
        composable(route = Screen.AdminDashboard.route) {
            com.example.scamazon_frontend.ui.screens.admin.dashboard.AdminDashboardScreen(
                onNavigateToChat = {
                    navController.navigate(Screen.AdminChatList.route)
                }
            )
        }

        composable(route = Screen.AdminChatList.route) {
            val context = LocalContext.current
            val viewModel: com.example.scamazon_frontend.ui.screens.admin.chat.AdminChatListViewModel = viewModel(factory = com.example.scamazon_frontend.di.ViewModelFactory(context))
            com.example.scamazon_frontend.ui.screens.admin.chat.AdminChatListScreen(
                viewModel = viewModel,
                onNavigateToChatDetail = { chatRoomId ->
                    navController.navigate(Screen.AdminChatDetail.createRoute(chatRoomId))
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.AdminChatDetail.route,
            arguments = listOf(
                navArgument("chatRoomId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val chatRoomId = backStackEntry.arguments?.getInt("chatRoomId") ?: 0
            val context = LocalContext.current
            val viewModel: com.example.scamazon_frontend.ui.screens.admin.chat.AdminChatDetailViewModel = viewModel(factory = com.example.scamazon_frontend.di.ViewModelFactory(context))
            com.example.scamazon_frontend.ui.screens.admin.chat.AdminChatDetailScreen(
                chatRoomId = chatRoomId,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(route = Screen.AdminProducts.route) {
            AdminProductListScreen(
                onNavigateToAddProduct = {
                    navController.navigate(Screen.AdminProductAdd.route)
                },
                onNavigateToEditProduct = { productSlug ->
                    navController.navigate(Screen.AdminProductEdit.createRoute(productSlug))
                }
            )
        }

        composable(route = Screen.AdminProductAdd.route) {
            AdminProductFormScreen(
                productSlug = null,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.AdminProductEdit.route,
            arguments = listOf(
                navArgument("productSlug") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val productSlug = backStackEntry.arguments?.getString("productSlug")
            AdminProductFormScreen(
                productSlug = productSlug,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = Screen.AdminCategories.route) {
            AdminCategoryListScreen(
                onNavigateToAddCategory = {
                    navController.navigate(Screen.AdminCategoryAdd.route)
                },
                onNavigateToEditCategory = { categoryId ->
                    navController.navigate(Screen.AdminCategoryEdit.createRoute(categoryId))
                },
                onNavigateToAddBrand = {
                    navController.navigate(Screen.AdminBrandAdd.route)
                },
                onNavigateToEditBrand = { brandId ->
                    navController.navigate(Screen.AdminBrandEdit.createRoute(brandId))
                }
            )
        }

        composable(route = Screen.AdminCategoryAdd.route) {
            AdminCategoryFormScreen(
                isBrand = false,
                editId = null,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.AdminCategoryEdit.route,
            arguments = listOf(
                navArgument("categoryId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId")?.toIntOrNull()
            AdminCategoryFormScreen(
                isBrand = false,
                editId = categoryId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = Screen.AdminBrandAdd.route) {
            AdminCategoryFormScreen(
                isBrand = true,
                editId = null,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.AdminBrandEdit.route,
            arguments = listOf(
                navArgument("brandId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val brandId = backStackEntry.arguments?.getString("brandId")?.toIntOrNull()
            AdminCategoryFormScreen(
                isBrand = true,
                editId = brandId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Admin Order Management
        composable(route = Screen.AdminOrders.route) {
            AdminOrderListScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onOrderClick = { orderId ->
                    navController.navigate(Screen.AdminOrderDetail.createRoute(orderId))
                }
            )
        }

        composable(
            route = Screen.AdminOrderDetail.route,
            arguments = listOf(
                navArgument(NavArgs.ORDER_ID) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString(NavArgs.ORDER_ID)?.toIntOrNull() ?: 0
            AdminOrderDetailScreen(
                orderId = orderId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = Screen.AdminAccount.route) {
            AdminAccountScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}

/**
 * Placeholder Screen for screens not yet implemented
 */
@Composable
private fun PlaceholderScreen(screenName: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$screenName\n(Coming Soon)",
            style = Typography.headlineMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}
