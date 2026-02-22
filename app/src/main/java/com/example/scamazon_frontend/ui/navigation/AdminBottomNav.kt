package com.example.scamazon_frontend.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.scamazon_frontend.ui.components.BottomNavItem

sealed class AdminBottomNavItem(
    route: String,
    title: String,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector
) : BottomNavItem(route, title, selectedIcon, unselectedIcon) {

    object Dashboard : AdminBottomNavItem(
        route = "admin_dashboard",
        title = "Dashboard",
        selectedIcon = Icons.Filled.Dashboard,
        unselectedIcon = Icons.Outlined.Dashboard
    )

    object Products : AdminBottomNavItem(
        route = "admin_products",
        title = "Products",
        selectedIcon = Icons.Filled.Inventory2,
        unselectedIcon = Icons.Outlined.Inventory2
    )

    object Categories : AdminBottomNavItem(
        route = "admin_categories",
        title = "Categories",
        selectedIcon = Icons.Filled.Category,
        unselectedIcon = Icons.Outlined.Category
    )

    object Orders : AdminBottomNavItem(
        route = "admin_orders",
        title = "Orders",
        selectedIcon = Icons.Filled.ShoppingCart,
        unselectedIcon = Icons.Outlined.ShoppingCart
    )

    object Account : AdminBottomNavItem(
        route = "admin_account",
        title = "Account",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
}

val adminBottomNavItems = listOf(
    AdminBottomNavItem.Dashboard,
    AdminBottomNavItem.Products,
    AdminBottomNavItem.Categories,
    AdminBottomNavItem.Orders,
    AdminBottomNavItem.Account
)
