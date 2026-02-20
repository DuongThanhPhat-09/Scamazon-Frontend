package com.example.scamazon_frontend.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.scamazon_frontend.ui.components.*
import com.example.scamazon_frontend.ui.theme.*

import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.scamazon_frontend.core.utils.Resource
import com.example.scamazon_frontend.data.models.category.CategoryDto
import com.example.scamazon_frontend.data.models.product.ProductDto
import com.example.scamazon_frontend.di.ViewModelFactory

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(factory = ViewModelFactory(LocalContext.current)),
    onNavigateToProductDetail: (String) -> Unit = {},
    onNavigateToSearch: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToWishlist: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    
    val categoriesState by viewModel.categoriesState.collectAsStateWithLifecycle()
    val flashSaleState by viewModel.flashSaleState.collectAsStateWithLifecycle()
    val megaSaleState by viewModel.megaSaleState.collectAsStateWithLifecycle()
    val recommendedState by viewModel.recommendedState.collectAsStateWithLifecycle()

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
                when (categoriesState) {
                    is Resource.Loading -> CircularProgressIndicator(modifier = Modifier.padding(Dimens.ScreenPadding))
                    is Resource.Success -> CategoriesRow(categoriesState.data ?: emptyList())
                    is Resource.Error -> Text("Error loading categories", modifier = Modifier.padding(Dimens.ScreenPadding))
                }
            }

            // Flash Sale Section
            item {
                Spacer(modifier = Modifier.height(24.dp))
                SectionHeader(
                    title = "Flash Sale",
                    onSeeAllClick = { /* Navigate to flash sale */ }
                )
                Spacer(modifier = Modifier.height(12.dp))
                when (flashSaleState) {
                    is Resource.Loading -> CircularProgressIndicator(modifier = Modifier.padding(Dimens.ScreenPadding))
                    is Resource.Success -> ProductsRow(flashSaleState.data?.items ?: emptyList(), onProductClick = onNavigateToProductDetail)
                    is Resource.Error -> Text("Error", modifier = Modifier.padding(Dimens.ScreenPadding))
                }
            }

            // Mega Sale Section
            item {
                Spacer(modifier = Modifier.height(24.dp))
                SectionHeader(
                    title = "Mega Sale",
                    onSeeAllClick = { /* Navigate to mega sale */ }
                )
                Spacer(modifier = Modifier.height(12.dp))
                when (megaSaleState) {
                    is Resource.Loading -> CircularProgressIndicator(modifier = Modifier.padding(Dimens.ScreenPadding))
                    is Resource.Success -> ProductsRow(megaSaleState.data?.items ?: emptyList(), onProductClick = onNavigateToProductDetail)
                    is Resource.Error -> Text("Error", modifier = Modifier.padding(Dimens.ScreenPadding))
                }
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
                when (recommendedState) {
                    is Resource.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    is Resource.Success -> ProductsGrid(recommendedState.data?.items ?: emptyList(), onProductClick = onNavigateToProductDetail)
                    is Resource.Error -> Text("Error loading products", modifier = Modifier.padding(Dimens.ScreenPadding))
                }
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
private fun CategoriesRow(categories: List<CategoryDto>) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = Dimens.ScreenPadding),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(categories) { category ->
            CategoryCard(
                name = category.name,
                icon = Icons.Default.Category, // Fallback icon since we use image_url normally
                onClick = { /* Navigate to category */ }
            )
        }
    }
}

@Composable
private fun ProductsRow(products: List<ProductDto>, onProductClick: (String) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = Dimens.ScreenPadding),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(products) { product ->
            ProductCard(
                productName = product.name,
                productImage = product.primaryImage ?: "",
                price = product.price,
                originalPrice = product.salePrice, // using sale_price as original for demo if needed, usually it's inverse
                discount = null, // calculate discount if needed
                rating = product.avgRating ?: 0f,
                onClick = { onProductClick(product.id.toString()) }
            )
        }
    }
}

@Composable
private fun ProductsGrid(products: List<ProductDto>, onProductClick: (String) -> Unit) {
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
                        productImage = product.primaryImage ?: "",
                        price = product.price,
                        originalPrice = product.salePrice,
                        discount = null,
                        rating = product.avgRating ?: 0f,
                        onClick = { onProductClick(product.id.toString()) },
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



@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    ScamazonFrontendTheme {
        HomeScreen()
    }
}
