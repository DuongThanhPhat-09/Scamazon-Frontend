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
 */
@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController(),
    cartItemCount: Int = 0
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Screens that should show bottom navigation
    val bottomNavScreens = listOf(
        Screen.Home.route,
        Screen.Explore.route,
        Screen.Cart.route,
        Screen.Offer.route,
        Screen.Account.route
    )

    // Check if current screen should show bottom nav
    val showBottomNav = currentRoute in bottomNavScreens

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        bottomBar = {
            if (showBottomNav) {
                LafyuuBottomNavBar(
                    currentRoute = currentRoute ?: Screen.Home.route,
                    onItemClick = { item ->
                        navController.navigate(item.route) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            popUpTo(Screen.Home.route) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    },
                    cartBadgeCount = cartItemCount
                )
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
