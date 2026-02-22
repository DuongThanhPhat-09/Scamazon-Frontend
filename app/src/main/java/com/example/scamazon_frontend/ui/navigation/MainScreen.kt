package com.example.scamazon_frontend.ui.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.scamazon_frontend.ui.components.BottomNavItem
import com.example.scamazon_frontend.ui.components.LafyuuBottomNavBar

/**
 * Main Screen with Bottom Navigation
 * Wraps the NavGraph with bottom navigation bar
 * Supports both Customer and Admin role-based navigation
 */
@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController(),
    cartItemCount: Int = 0
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Customer screens that should show bottom navigation
    val customerBottomNavScreens = listOf(
        Screen.Home.route,
        Screen.Explore.route,
        Screen.Cart.route,
        Screen.Offer.route,
        Screen.Account.route
    )

    // Admin screens that should show bottom navigation
    val adminBottomNavScreens = listOf(
        Screen.AdminDashboard.route,
        Screen.AdminProducts.route,
        Screen.AdminCategories.route,
        Screen.AdminAccount.route
    )

    val showCustomerBottomNav = currentRoute in customerBottomNavScreens
    val showAdminBottomNav = currentRoute in adminBottomNavScreens

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        bottomBar = {
            when {
                showAdminBottomNav -> {
                    LafyuuBottomNavBar(
                        items = adminBottomNavItems,
                        currentRoute = currentRoute ?: Screen.AdminDashboard.route,
                        onItemClick = { item ->
                            navController.navigate(item.route) {
                                popUpTo(Screen.AdminDashboard.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
                showCustomerBottomNav -> {
                    LafyuuBottomNavBar(
                        currentRoute = currentRoute ?: Screen.Home.route,
                        onItemClick = { item ->
                            navController.navigate(item.route) {
                                popUpTo(Screen.Home.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        cartBadgeCount = cartItemCount
                    )
                }
            }
        }
    ) { innerPadding ->
        NavGraph(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

/**
 * Check if the given route matches bottom nav item route
 */
fun getBottomNavItem(route: String?): BottomNavItem? {
    return when (route) {
        Screen.Home.route -> BottomNavItem.Home
        Screen.Explore.route -> BottomNavItem.Explore
        Screen.Cart.route -> BottomNavItem.Cart
        Screen.Offer.route -> BottomNavItem.Offer
        Screen.Account.route -> BottomNavItem.Account
        else -> null
    }
}
