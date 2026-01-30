package com.example.scamazon_frontend.ui.screens.product

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scamazon_frontend.ui.components.*
import com.example.scamazon_frontend.ui.theme.*

@Composable
fun ProductDetailScreen(
    productId: String,
    onNavigateBack: () -> Unit = {},
    onNavigateToCart: () -> Unit = {},
    onNavigateToReview: () -> Unit = {}
) {
    var selectedSize by remember { mutableStateOf("42") }
    var selectedColor by remember { mutableStateOf(PrimaryBlue) }
    var quantity by remember { mutableStateOf(1) }
    var isFavorite by remember { mutableStateOf(false) }

    // Sample product data
    val product = SampleProductDetail(
        id = productId,
        name = "Nike Air Zoom Pegasus 36 Miami",
        description = "The Nike Air Zoom Pegasus 36 Miami is a comfortable and stylish running shoe designed for everyday use. Features responsive cushioning and breathable mesh upper.",
        price = 299.43,
        originalPrice = 534.33,
        discount = 24,
        rating = 4.5f,
        reviewCount = 5,
        sizes = listOf("39", "40", "41", "42", "43", "44"),
        colors = listOf(PrimaryBlue, AccentGold, StatusSuccess, StatusError)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
    ) {
        // Top App Bar
        LafyuuCartAppBar(
            title = product.name,
            onBackClick = onNavigateBack,
            cartItemCount = 2,
            onCartClick = onNavigateToCart
        )

        // Content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            // Product Images
            ProductImageSection(
                isFavorite = isFavorite,
                onFavoriteClick = { isFavorite = !isFavorite }
            )

            // Product Info
            Column(
                modifier = Modifier.padding(Dimens.ScreenPadding)
            ) {
                // Name & Favorite
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = product.name,
                        style = Typography.headlineMedium,
                        color = TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Rating
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RatingBar(rating = product.rating)
                    Text(
                        text = "(${product.reviewCount} Reviews)",
                        style = Typography.bodySmall,
                        color = TextSecondary,
                        modifier = Modifier.clickable { onNavigateToReview() }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Price
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "$${String.format("%.2f", product.price)}",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = PrimaryBlue
                    )

                    product.originalPrice?.let {
                        Text(
                            text = "$${String.format("%.2f", it)}",
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            color = TextHint,
                            textDecoration = TextDecoration.LineThrough
                        )
                    }

                    product.discount?.let {
                        Text(
                            text = "$it% Off",
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = SecondaryRed
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Select Size
                Text(
                    text = "Select Size",
                    style = Typography.titleMedium,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    product.sizes.forEach { size ->
                        SizeChip(
                            size = size,
                            isSelected = selectedSize == size,
                            onClick = { selectedSize = size }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Select Color
                Text(
                    text = "Select Color",
                    style = Typography.titleMedium,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    product.colors.forEach { color ->
                        ColorChip(
                            color = color,
                            isSelected = selectedColor == color,
                            onClick = { selectedColor = color }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Quantity
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Quantity",
                        style = Typography.titleMedium,
                        color = TextPrimary
                    )

                    QuantitySelector(
                        quantity = quantity,
                        onIncrease = { quantity++ },
                        onDecrease = { if (quantity > 1) quantity-- }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Specification
                Text(
                    text = "Specification",
                    style = Typography.titleMedium,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                SpecificationRow("Shown", "Laser Blue/Watermelon/White")
                SpecificationRow("Style", "CD0113-400")
                SpecificationRow("Material", "Mesh, Rubber")

                Spacer(modifier = Modifier.height(24.dp))

                // Description
                Text(
                    text = "Description",
                    style = Typography.titleMedium,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = product.description,
                    style = Typography.bodyLarge,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Review Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Review (${product.reviewCount})",
                        style = Typography.titleMedium,
                        color = TextPrimary
                    )

                    LafyuuTextButton(
                        text = "See All",
                        onClick = onNavigateToReview
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Bottom Add to Cart Button
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shadowElevation = 8.dp,
            color = White
        ) {
            LafyuuPrimaryButton(
                text = "Add To Cart",
                onClick = {
                    // TODO: Add to cart logic
                    onNavigateToCart()
                },
                modifier = Modifier.padding(Dimens.ScreenPadding)
            )
        }
    }
}

@Composable
private fun ProductImageSection(
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(238.dp)
            .background(BackgroundLight)
    ) {
        // Main Image Placeholder
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Product Image",
                style = Typography.bodyLarge,
                color = TextHint
            )
        }

        // Favorite Button
        IconButton(
            onClick = onFavoriteClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Favorite",
                tint = if (isFavorite) SecondaryRed else TextHint
            )
        }

        // Thumbnail Row
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(4) { index ->
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            if (index == 0) PrimaryBlueSoft else White,
                            LafyuuShapes.ImageShape
                        )
                        .border(
                            width = if (index == 0) 2.dp else 1.dp,
                            color = if (index == 0) PrimaryBlue else BorderLight,
                            shape = LafyuuShapes.ImageShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${index + 1}",
                        style = Typography.bodySmall,
                        color = TextHint
                    )
                }
            }
        }
    }
}

@Composable
private fun SizeChip(
    size: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(if (isSelected) PrimaryBlue else White)
            .border(
                width = 1.dp,
                color = if (isSelected) PrimaryBlue else BorderLight,
                shape = CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = size,
            fontFamily = Poppins,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 14.sp,
            color = if (isSelected) White else TextPrimary
        )
    }
}

@Composable
private fun ColorChip(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(color)
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) TextPrimary else BorderLight,
                shape = CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun SpecificationRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = Typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = Typography.bodyMedium,
            color = TextPrimary,
            modifier = Modifier.weight(2f)
        )
    }
}

// Sample data class
private data class SampleProductDetail(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val originalPrice: Double?,
    val discount: Int?,
    val rating: Float,
    val reviewCount: Int,
    val sizes: List<String>,
    val colors: List<Color>
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProductDetailScreenPreview() {
    ScamazonFrontendTheme {
        ProductDetailScreen(productId = "1")
    }
}
