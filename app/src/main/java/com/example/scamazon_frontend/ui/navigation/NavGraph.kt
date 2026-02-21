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
import com.example.scamazon_frontend.ui.screens.auth.LoginScreen
import com.example.scamazon_frontend.ui.screens.auth.RegisterScreen
import com.example.scamazon_frontend.ui.screens.cart.CartScreen
import com.example.scamazon_frontend.ui.screens.checkout.CheckoutScreen
import com.example.scamazon_frontend.ui.screens.home.HomeScreen
import com.example.scamazon_frontend.ui.screens.order.OrderHistoryScreen
import com.example.scamazon_frontend.ui.screens.product.ProductDetailScreen
import com.example.scamazon_frontend.ui.screens.product.ProductListScreen
import com.example.scamazon_frontend.ui.screens.profile.AccountScreen
import com.example.scamazon_frontend.ui.screens.profile.EditProfileScreen
import com.example.scamazon_frontend.ui.screens.search.SearchScreen
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
                onNavigateToReview = {
                    navController.navigate(Screen.Review.createRoute(productId))
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
        // PLACEHOLDER SCREENS (To be implemented)
        // ==========================================
        composable(route = Screen.Explore.route) {
            ProductListScreen(
                categoryId = null,
                categoryName = "Explore",
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToProductDetail = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                }
            )
        }

        composable(route = Screen.Offer.route) {
            PlaceholderScreen(screenName = "Offers")
        }

        composable(route = Screen.Search.route) {
            SearchScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToSearchResult = { query ->
                    navController.navigate(Screen.SearchResult.createRoute(query))
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

        composable(route = Screen.Checkout.route) {
            CheckoutScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onOrderSuccess = {
                    navController.navigate(Screen.OrderSuccess.createRoute("NEW")) {
                        popUpTo(Screen.Cart.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.OrderSuccess.route,
            arguments = listOf(
                navArgument(NavArgs.ORDER_ID) { type = NavType.StringType }
            )
        ) {
            PlaceholderScreen(screenName = "Order Placed!")
        }

        composable(route = Screen.Profile.route) {
            EditProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSaveSuccess = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = Screen.EditProfile.route) {
            EditProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSaveSuccess = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = Screen.OrderHistory.route) {
            OrderHistoryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToOrderDetail = { orderId ->
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
            val orderId = backStackEntry.arguments?.getString(NavArgs.ORDER_ID) ?: ""
            PlaceholderScreen(screenName = "Order $orderId")
        }

        composable(route = Screen.Wishlist.route) {
            PlaceholderScreen(screenName = "Wishlist")
        }

        composable(route = Screen.Notifications.route) {
            PlaceholderScreen(screenName = "Notifications")
        }

        composable(route = Screen.Settings.route) {
            PlaceholderScreen(screenName = "Settings")
        }

        composable(route = Screen.Map.route) {
            PlaceholderScreen(screenName = "Store Location")
        }

        composable(route = Screen.Chat.route) {
            PlaceholderScreen(screenName = "Chat Support")
        }

        composable(route = Screen.ForgotPassword.route) {
            PlaceholderScreen(screenName = "Forgot Password")
        }

        composable(
            route = Screen.Review.route,
            arguments = listOf(
                navArgument(NavArgs.PRODUCT_ID) { type = NavType.StringType }
            )
        ) {
            PlaceholderScreen(screenName = "Reviews")
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
