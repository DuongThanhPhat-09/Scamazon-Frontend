package com.example.scamazon_frontend.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scamazon_frontend.ui.theme.*

/**
 * Main App Bar - Lafyuu Style
 * With search bar
 */
@Composable
fun LafyuuMainAppBar(
    searchQuery: String = "",
    onSearchQueryChange: (String) -> Unit = {},
    onSearchClick: () -> Unit = {},
    onFavoriteClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onMapClick: () -> Unit = {},
    onChatClick: () -> Unit = {},
    notificationBadge: Int = 0,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = BackgroundWhite,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Search Field
            LafyuuSearchField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                onSearch = onSearchClick,
                modifier = Modifier.weight(1f)
            )

            // Favorite Icon
            IconButton(onClick = onFavoriteClick) {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = "Favorites",
                    tint = TextSecondary
                )
            }

            // Map Location Icon
            IconButton(onClick = onMapClick) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Map",
                    tint = TextSecondary
                )
            }

            // Chat Support Icon
            IconButton(onClick = onChatClick) {
                Icon(
                    imageVector = Icons.Default.ChatBubbleOutline,
                    contentDescription = "Chat",
                    tint = TextSecondary
                )
            }

            // Notification Icon with Badge
            BadgedBox(
                badge = {
                    if (notificationBadge > 0) {
                        Badge(
                            containerColor = AccentGold,
                            contentColor = White
                        ) {
                            Text(
                                text = if (notificationBadge > 99) "99+" else notificationBadge.toString(),
                                fontSize = 10.sp,
                                fontFamily = Poppins
                            )
                        }
                    }
                }
            ) {
                IconButton(onClick = onNotificationClick) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = TextSecondary
                    )
                }
            }
        }
    }
}

/**
 * Simple App Bar - Lafyuu Style
 * With title and back button
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LafyuuTopAppBar(
    title: String,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextPrimary
            )
        },
        navigationIcon = {
            if (onBackClick != null) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Navy
                    )
                }
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = BackgroundWhite
        ),
        modifier = modifier
    )
}

/**
 * Cart App Bar - Lafyuu Style
 * With cart icon and badge
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LafyuuCartAppBar(
    title: String,
    onBackClick: () -> Unit,
    cartItemCount: Int = 0,
    onCartClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextPrimary
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Navy
                )
            }
        },
        actions = {
            BadgedBox(
                badge = {
                    if (cartItemCount > 0) {
                        Badge(
                            containerColor = AccentGold,
                            contentColor = White
                        ) {
                            Text(
                                text = if (cartItemCount > 99) "99+" else cartItemCount.toString(),
                                fontSize = 10.sp,
                                fontFamily = Poppins
                            )
                        }
                    }
                }
            ) {
                IconButton(onClick = onCartClick) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Cart",
                        tint = TextSecondary
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = BackgroundWhite
        ),
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun AppBarsPreview() {
    ScamazonFrontendTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LafyuuMainAppBar(
                notificationBadge = 5
            )

            LafyuuTopAppBar(
                title = "Product Details",
                onBackClick = {}
            )

            LafyuuCartAppBar(
                title = "Your Cart",
                onBackClick = {},
                cartItemCount = 3
            )
        }
    }
}
