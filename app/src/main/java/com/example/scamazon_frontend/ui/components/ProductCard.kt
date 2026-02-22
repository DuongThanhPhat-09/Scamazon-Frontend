package com.example.scamazon_frontend.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.scamazon_frontend.core.utils.formatPrice
import com.example.scamazon_frontend.ui.theme.*

/**
 * Product Card - Lafyuu Style
 * Used in Product List/Grid
 */
@Composable
fun ProductCard(
    productName: String,
    productImage: String,
    price: Double,
    originalPrice: Double? = null,
    rating: Float = 0f,
    discount: Int? = null,
    isFavorite: Boolean = false,
    onFavoriteClick: () -> Unit = {},
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(Dimens.ProductCardWidth)
            .clickable { onClick() },
        shape = LafyuuShapes.CardShape,
        colors = CardDefaults.cardColors(
            containerColor = BackgroundWhite
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
    ) {
        Column {
            // Product Image with Favorite & Discount Badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.ProductCardImageHeight)
            ) {
                // Product Image
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(productImage)
                        .crossfade(true)
                        .build(),
                    contentDescription = productName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BackgroundLight)
                        .clip(RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp))
                )

                // Discount Badge
                discount?.let {
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .background(
                                color = AccentGold,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .align(Alignment.TopStart)
                    ) {
                        Text(
                            text = "$it% Off",
                            color = White,
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                }

                // Favorite Icon
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) SecondaryRed else TextHint,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Product Info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                // Product Name
                Text(
                    text = productName,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Rating
                if (rating > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        repeat(5) { index ->
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (index < rating) AccentGold else StarEmpty,
                                modifier = Modifier.size(Dimens.StarSize)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Price
                Text(
                    text = "${formatPrice(price)}đ",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = PrimaryBlue
                )

                // Original Price (if discounted)
                originalPrice?.let {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${formatPrice(it)}đ",
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Normal,
                            fontSize = 10.sp,
                            color = TextHint,
                            textDecoration = TextDecoration.LineThrough
                        )
                        discount?.let { disc ->
                            Text(
                                text = "$disc% Off",
                                fontFamily = Poppins,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = AccentGold
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Horizontal Product Card - Lafyuu Style
 * Used in Cart, Wishlist, Order History
 */
@Composable
fun ProductCardHorizontal(
    productName: String,
    productImage: String,
    price: Double,
    quantity: Int = 1,
    isFavorite: Boolean = false,
    onFavoriteClick: () -> Unit = {},
    onRemoveClick: () -> Unit = {},
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = LafyuuShapes.CardShape,
        colors = CardDefaults.cardColors(
            containerColor = BackgroundWhite
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Product Image
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(productImage)
                    .crossfade(true)
                    .build(),
                contentDescription = productName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(Dimens.ProductImageSize)
                    .background(BackgroundLight, RoundedCornerShape(5.dp))
                    .clip(RoundedCornerShape(5.dp))
            )

            // Product Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Name & Favorite/Remove
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = productName,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = TextPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Row {
                        IconButton(
                            onClick = onFavoriteClick,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (isFavorite) SecondaryRed else TextHint,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        IconButton(
                            onClick = onRemoveClick,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Remove",
                                tint = TextHint,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Price & Quantity
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${formatPrice(price)}đ",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = PrimaryBlue
                    )

                    // Quantity Badge
                    Box(
                        modifier = Modifier
                            .background(
                                color = BackgroundLight,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Qty: $quantity",
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Normal,
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductCardsPreview() {
    ScamazonFrontendTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProductCard(
                productName = "Nike Air Max 270 React ENG",
                productImage = "",
                price = 299.43,
                originalPrice = 534.33,
                rating = 4f,
                discount = 24,
                isFavorite = true
            )

            ProductCardHorizontal(
                productName = "Nike Air Zoom Pegasus 36 Miami",
                productImage = "",
                price = 299.43,
                quantity = 2,
                isFavorite = false
            )
        }
    }
}
