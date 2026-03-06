package com.example.scamazon_frontend.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scamazon_frontend.ui.theme.*

/**
 * Bottom Navigation Items
 */
open class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Home : BottomNavItem(
        route = "home",
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )

    object Explore : BottomNavItem(
        route = "explore",
        title = "Explore",
        selectedIcon = Icons.Filled.Search,
        unselectedIcon = Icons.Outlined.Search
    )

    object Cart : BottomNavItem(
        route = "cart",
        title = "Cart",
        selectedIcon = Icons.Filled.ShoppingCart,
        unselectedIcon = Icons.Outlined.ShoppingCart
    )

    object Account : BottomNavItem(
        route = "account",
        title = "Account",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
}

/**
 * Bottom Navigation Bar - Lafyuu Style
 */
@Composable
fun LafyuuBottomNavBar(
    items: List<BottomNavItem> = listOf(
        BottomNavItem.Home,
        BottomNavItem.Explore,
        BottomNavItem.Cart,
        BottomNavItem.Account
    ),
    currentRoute: String,
    onItemClick: (BottomNavItem) -> Unit,
    cartBadgeCount: Int = 0,
    modifier: Modifier = Modifier
) {
    // Bounce animation for badge
    val badgeScale = remember { Animatable(1f) }
    LaunchedEffect(cartBadgeCount) {
        if (cartBadgeCount > 0) {
            badgeScale.snapTo(1.5f)
            badgeScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        }
    }

    NavigationBar(
        modifier = modifier,
        containerColor = BackgroundWhite,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                selected = isSelected,
                onClick = { onItemClick(item) },
                icon = {
                    if (item == BottomNavItem.Cart && cartBadgeCount > 0) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = AccentGold,
                                    contentColor = White,
                                    modifier = Modifier.scale(badgeScale.value)
                                ) {
                                    Text(
                                        text = if (cartBadgeCount > 99) "99+" else cartBadgeCount.toString(),
                                        fontSize = 10.sp,
                                        fontFamily = Poppins
                                    )
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.title,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    } else {
                        Icon(
                            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.title,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                label = {
                    Text(
                        text = item.title,
                        fontFamily = Poppins,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 10.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryBlue,
                    selectedTextColor = PrimaryBlue,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary,
                    indicatorColor = PrimaryBlueSoft
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomNavBarPreview() {
    ScamazonFrontendTheme {
        LafyuuBottomNavBar(
            currentRoute = "home",
            onItemClick = {},
            cartBadgeCount = 3
        )
    }
}
