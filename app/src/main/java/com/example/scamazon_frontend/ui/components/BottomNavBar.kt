package com.example.scamazon_frontend.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
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

    object Trends : BottomNavItem(
        route = "offer",
        title = "Trends",
        selectedIcon = Icons.Filled.Whatshot,
        unselectedIcon = Icons.Filled.Whatshot
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


private val NAV_BAR_HEIGHT  = 64.dp
private val FAB_SIZE        = 54.dp
private val FAB_OVERHANG    = 20.dp   // how much FAB sticks above the nav bar
private val TOTAL_HEIGHT    = NAV_BAR_HEIGHT + FAB_OVERHANG


@Composable
fun LafyuuBottomNavBar(
    items: List<BottomNavItem> = listOf(
        BottomNavItem.Home,
        BottomNavItem.Explore,
        BottomNavItem.Trends,
        BottomNavItem.Cart,
        BottomNavItem.Account
    ),
    currentRoute: String,
    onItemClick: (BottomNavItem) -> Unit,
    cartBadgeCount: Int = 0,
    modifier: Modifier = Modifier
) {
    // Badge bounce animation
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

    // Split items: [left 2] [center] [right 2]
    val leftItems  = items.take(2)
    val centerItem = items.getOrNull(2)
    val rightItems = items.drop(3)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(TOTAL_HEIGHT)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(NAV_BAR_HEIGHT)
                .align(Alignment.BottomCenter),
            color = BackgroundWhite,
            shadowElevation = 10.dp,
            tonalElevation = 0.dp
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left items
                leftItems.forEach { item ->
                    NavTabItem(
                        item = item,
                        isSelected = currentRoute == item.route,
                        modifier = Modifier.weight(1f),
                        onClick = { onItemClick(item) }
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    centerItem?.let {
                        Text(
                            text = it.title,
                            fontFamily = Poppins,
                            fontWeight = if (currentRoute == it.route) FontWeight.SemiBold else FontWeight.Normal,
                            fontSize = 10.sp,
                            color = if (currentRoute == it.route) PrimaryBlue else TextSecondary,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                    }
                }

                // Right items
                rightItems.forEach { item ->
                    val isCart = item == BottomNavItem.Cart
                    NavTabItem(
                        item = item,
                        isSelected = currentRoute == item.route,
                        modifier = Modifier.weight(1f),
                        cartBadgeCount = if (isCart) cartBadgeCount else 0,
                        badgeScale = if (isCart) badgeScale.value else 1f,
                        onClick = { onItemClick(item) }
                    )
                }
            }
        }

        centerItem?.let { fab ->
            val isSelected = currentRoute == fab.route
            Box(
                modifier = Modifier
                    .size(FAB_SIZE)
                    .align(Alignment.TopCenter)
                    .shadow(elevation = 8.dp, shape = CircleShape)
                    .clip(CircleShape)
                    .background(if (isSelected) PrimaryBlue else PrimaryBlueDark)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onItemClick(fab) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = fab.selectedIcon,
                    contentDescription = fab.title,
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}


@Composable
private fun NavTabItem(
    item: BottomNavItem,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    cartBadgeCount: Int = 0,
    badgeScale: Float = 1f,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val iconColor = if (isSelected) PrimaryBlue else TextSecondary
        val icon = if (isSelected) item.selectedIcon else item.unselectedIcon

        if (cartBadgeCount > 0) {
            BadgedBox(
                badge = {
                    Badge(
                        containerColor = AccentGold,
                        contentColor = Color.White,
                        modifier = Modifier.scale(badgeScale)
                    ) {
                        Text(
                            text = if (cartBadgeCount > 99) "99+" else cartBadgeCount.toString(),
                            fontSize = 9.sp,
                            fontFamily = Poppins
                        )
                    }
                }
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = item.title,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        } else {
            Icon(
                imageVector = icon,
                contentDescription = item.title,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = item.title,
            fontFamily = Poppins,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            fontSize = 10.sp,
            color = iconColor
        )
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
