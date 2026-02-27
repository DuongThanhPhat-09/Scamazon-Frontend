package com.example.scamazon_frontend.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scamazon_frontend.ui.theme.*

/**
 * Main App Bar - Two-row layout
 * Row 1: Brand name + action icons
 * Row 2: Full-width clickable search bar
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
        color = PrimaryBlue,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Row 1: Brand + Icons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 8.dp, top = 10.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Brand Name
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(
                            fontWeight = FontWeight.Light,
                            color = White
                        )) {
                            append("Scam")
                        }
                        withStyle(style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = AccentGoldLight
                        )) {
                            append("azon")
                        }
                    },
                    fontFamily = Poppins,
                    fontSize = 22.sp,
                    letterSpacing = 1.sp
                )

                // Action Icons
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onFavoriteClick,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = "Favorites",
                            tint = White.copy(alpha = 0.9f),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    IconButton(
                        onClick = onChatClick,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChatBubbleOutline,
                            contentDescription = "Chat",
                            tint = White.copy(alpha = 0.9f),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    IconButton(
                        onClick = onMapClick,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Map",
                            tint = White.copy(alpha = 0.9f),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    BadgedBox(
                        badge = {
                            if (notificationBadge > 0) {
                                Badge(
                                    containerColor = AccentGold,
                                    contentColor = White
                                ) {
                                    Text(
                                        text = if (notificationBadge > 99) "99+" else notificationBadge.toString(),
                                        fontSize = 9.sp,
                                        fontFamily = Poppins
                                    )
                                }
                            }
                        }
                    ) {
                        IconButton(
                            onClick = onNotificationClick,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = White.copy(alpha = 0.9f),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            // Row 2: Search Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
                    .clickable { onSearchClick() },
                shape = LafyuuShapes.SearchBarShape,
                color = White
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = TextHint,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Search products...",
                        fontFamily = Poppins,
                        fontSize = 14.sp,
                        color = TextHint
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
