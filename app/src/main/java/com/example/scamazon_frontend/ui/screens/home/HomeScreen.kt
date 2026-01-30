package com.example.scamazon_frontend.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.scamazon_frontend.ui.components.*
import com.example.scamazon_frontend.ui.theme.*

@Composable
fun HomeScreen(
    onNavigateToProductDetail: (String) -> Unit = {},
    onNavigateToSearch: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToWishlist: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
            .statusBarsPadding()
    ) {
        // Top App Bar with Search
        LafyuuMainAppBar(
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            onSearchClick = onNavigateToSearch,
            onFavoriteClick = onNavigateToWishlist,
            onNotificationClick = onNavigateToNotifications,
            notificationBadge = 3
        )

        // Content
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // Banner/Carousel
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SaleBannerCard(
                    title = "Super Flash Sale",
                    subtitle = "50% Off",
                    discount = "Get Now",
                    modifier = Modifier.padding(horizontal = Dimens.ScreenPadding)
                )
            }

            // Categories Section
            item {
                Spacer(modifier = Modifier.height(24.dp))
                SectionHeader(
                    title = "Category",
                    onSeeAllClick = { /* Navigate to categories */ }
                )
                Spacer(modifier = Modifier.height(12.dp))
                CategoriesRow()
            }

            // Flash Sale Section
            item {
                Spacer(modifier = Modifier.height(24.dp))
                SectionHeader(
                    title = "Flash Sale",
                    onSeeAllClick = { /* Navigate to flash sale */ }
                )
                Spacer(modifier = Modifier.height(12.dp))
                ProductsRow(onProductClick = onNavigateToProductDetail)
            }

            // Mega Sale Section
            item {
                Spacer(modifier = Modifier.height(24.dp))
                SectionHeader(
                    title = "Mega Sale",
                    onSeeAllClick = { /* Navigate to mega sale */ }
                )
                Spacer(modifier = Modifier.height(12.dp))
                ProductsRow(onProductClick = onNavigateToProductDetail)
            }

            // Recommended Section
            item {
                Spacer(modifier = Modifier.height(24.dp))
                SaleBannerCard(
                    title = "Recommended",
                    subtitle = "We recommend the best for you",
                    discount = "Shop Now",
                    backgroundColor = SecondaryPurple,
                    modifier = Modifier.padding(horizontal = Dimens.ScreenPadding)
                )
            }

            // Products Grid
            item {
                Spacer(modifier = Modifier.height(24.dp))
                ProductsGrid(onProductClick = onNavigateToProductDetail)
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    onSeeAllClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.ScreenPadding),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = Typography.titleLarge,
            color = TextPrimary
        )
        LafyuuTextButton(
            text = "See All",
            onClick = onSeeAllClick
        )
    }
}

@Composable
private fun CategoriesRow() {
    val categories = listOf(
        "Man Shirt" to Icons.Default.Person,
        "Dress" to Icons.Default.Checkroom,
        "Man Work" to Icons.Default.Work,
        "Woman Bag" to Icons.Default.ShoppingBag,
        "Man Shoes" to Icons.Default.IceSkating,
        "High Heels" to Icons.Default.Woman
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = Dimens.ScreenPadding),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(categories) { (name, icon) ->
            CategoryCard(
                name = name,
                icon = icon,
                onClick = { /* Navigate to category */ }
            )
        }
    }
}

@Composable
private fun ProductsRow(onProductClick: (String) -> Unit) {
    // Sample products
    val products = listOf(
        SampleProduct("1", "Nike Air Max 270 React ENG", 299.43, 534.33, 24, 4.5f),
        SampleProduct("2", "Nike Air Max 270 React", 299.43, null, null, 4.0f),
        SampleProduct("3", "Nike Air Max 90", 199.99, 299.99, 33, 4.8f),
        SampleProduct("4", "Adidas Ultraboost 21", 249.99, null, null, 4.2f)
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = Dimens.ScreenPadding),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(products) { product ->
            ProductCard(
                productName = product.name,
                productImage = "",
                price = product.price,
                originalPrice = product.originalPrice,
                discount = product.discount,
                rating = product.rating,
                onClick = { onProductClick(product.id) }
            )
        }
    }
}

@Composable
private fun ProductsGrid(onProductClick: (String) -> Unit) {
    // Sample products for grid
    val products = listOf(
        SampleProduct("5", "Nike Air Max 270 React ENG", 299.43, 534.33, 24, 4.5f),
        SampleProduct("6", "Nike Air Max 270 React", 299.43, null, null, 4.0f),
        SampleProduct("7", "Nike Air Max 90", 199.99, 299.99, 33, 4.8f),
        SampleProduct("8", "Adidas Ultraboost 21", 249.99, null, null, 4.2f),
        SampleProduct("9", "Puma RS-X", 159.99, 199.99, 20, 4.1f),
        SampleProduct("10", "New Balance 574", 89.99, null, null, 4.6f)
    )

    Column(
        modifier = Modifier.padding(horizontal = Dimens.ScreenPadding)
    ) {
        products.chunked(2).forEach { rowProducts ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                rowProducts.forEach { product ->
                    ProductCard(
                        productName = product.name,
                        productImage = "",
                        price = product.price,
                        originalPrice = product.originalPrice,
                        discount = product.discount,
                        rating = product.rating,
                        onClick = { onProductClick(product.id) },
                        modifier = Modifier.weight(1f)
                    )
                }
                // Fill empty space if odd number of products
                if (rowProducts.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// Sample data class for preview
private data class SampleProduct(
    val id: String,
    val name: String,
    val price: Double,
    val originalPrice: Double?,
    val discount: Int?,
    val rating: Float
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    ScamazonFrontendTheme {
        HomeScreen()
    }
}
