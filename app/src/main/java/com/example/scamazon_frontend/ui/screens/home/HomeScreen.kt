package com.example.scamazon_frontend.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.scamazon_frontend.ui.components.*
import com.example.scamazon_frontend.ui.theme.*

import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.scamazon_frontend.core.utils.Resource

import com.example.scamazon_frontend.data.models.category.CategoryDto
import com.example.scamazon_frontend.data.models.product.ProductDto
import com.example.scamazon_frontend.di.ViewModelFactory
import com.example.scamazon_frontend.ui.screens.favorite.FavoriteViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(factory = ViewModelFactory(LocalContext.current)),
    favoriteViewModel: FavoriteViewModel = viewModel(factory = ViewModelFactory(LocalContext.current)),
    onNavigateToProductDetail: (String) -> Unit = {},
    onNavigateToCategory: (Int) -> Unit = {},
    onNavigateToProductList: (categoryId: String?, title: String) -> Unit = { _, _ -> },
    onNavigateToSearch: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToWishlist: () -> Unit = {},
    onNavigateToMap: () -> Unit = {},
    onNavigateToChat: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    

    val categoriesState by viewModel.categoriesState.collectAsStateWithLifecycle()
    val flashSaleState by viewModel.flashSaleState.collectAsStateWithLifecycle()
    val megaSaleState by viewModel.megaSaleState.collectAsStateWithLifecycle()
    val recommendedState by viewModel.recommendedState.collectAsStateWithLifecycle()
    val favoriteIds by favoriteViewModel.favoriteIds.collectAsStateWithLifecycle()

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
            onMapClick = onNavigateToMap,
            onChatClick = onNavigateToChat,
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

            // Category Section
            item {
                Spacer(modifier = Modifier.height(24.dp))
                SectionHeader(
                    title = "Category",
                    onSeeAllClick = { onNavigateToProductList(null, "All Products") }
                )
                Spacer(modifier = Modifier.height(12.dp))
                when (categoriesState) {
                    is Resource.Loading -> CircularProgressIndicator(modifier = Modifier.padding(Dimens.ScreenPadding))
                    is Resource.Success -> CategoriesRow(
                        categories = (categoriesState as Resource.Success<List<CategoryDto>>).data ?: emptyList(),
                        onCategoryClick = onNavigateToCategory
                    )
                    is Resource.Error -> Text("Error", modifier = Modifier.padding(Dimens.ScreenPadding))
                }
            }

            // Flash Sale Section
            item {
                Spacer(modifier = Modifier.height(24.dp))
                SectionHeader(
                    title = "Flash Sale",
                    onSeeAllClick = { onNavigateToProductList(null, "Flash Sale") }
                )
                Spacer(modifier = Modifier.height(12.dp))
                when (flashSaleState) {
                    is Resource.Loading -> CircularProgressIndicator(modifier = Modifier.padding(Dimens.ScreenPadding))
                    is Resource.Success -> ProductsRow(
                        products = flashSaleState.data?.items ?: emptyList(),
                        onProductClick = onNavigateToProductDetail,
                        favoriteIds = favoriteIds,
                        onToggleFavorite = { favoriteViewModel.toggleFavorite(it) }
                    )
                    is Resource.Error -> Text("Error", modifier = Modifier.padding(Dimens.ScreenPadding))
                }
            }

            // Mega Sale Section
            item {
                Spacer(modifier = Modifier.height(24.dp))
                SectionHeader(
                    title = "Mega Sale",
                    onSeeAllClick = { onNavigateToProductList(null, "Mega Sale") }
                )
                Spacer(modifier = Modifier.height(12.dp))
                when (megaSaleState) {
                    is Resource.Loading -> CircularProgressIndicator(modifier = Modifier.padding(Dimens.ScreenPadding))
                    is Resource.Success -> ProductsRow(
                        products = megaSaleState.data?.items ?: emptyList(),
                        onProductClick = onNavigateToProductDetail,
                        favoriteIds = favoriteIds,
                        onToggleFavorite = { favoriteViewModel.toggleFavorite(it) }
                    )
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
                    is Resource.Success -> ProductsGrid(
                        products = recommendedState.data?.items ?: emptyList(),
                        onProductClick = onNavigateToProductDetail,
                        favoriteIds = favoriteIds,
                        onToggleFavorite = { favoriteViewModel.toggleFavorite(it) }
                    )
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
private fun ProductsRow(
    products: List<ProductDto>,
    onProductClick: (String) -> Unit,
    favoriteIds: Set<Int> = emptySet(),
    onToggleFavorite: (Int) -> Unit = {}
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = Dimens.ScreenPadding),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(products) { product ->
            ProductCard(
                productName = product.name,
                productImage = product.primaryImage ?: "",
                price = product.price,
                originalPrice = product.salePrice,
                discount = null,
                rating = product.avgRating ?: 0f,
                isFavorite = favoriteIds.contains(product.id),
                onFavoriteClick = { onToggleFavorite(product.id) },
                onClick = { onProductClick(product.slug) }
            )
        }
    }
}

@Composable
private fun CategoriesRow(
    categories: List<CategoryDto>,
    onCategoryClick: (Int) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = Dimens.ScreenPadding),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(categories) { category ->
            Column(
                modifier = Modifier
                    .width(70.dp)
                    .clickable { onCategoryClick(category.id) },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Circular Image Container
                Box(
                    modifier = Modifier
                        .size(Dimens.CategoryCardSize)
                        .clip(CircleShape)
                        .background(PrimaryBlueSoft)
                        .border(
                            width = 1.dp,
                            color = BorderLight,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (!category.imageUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = category.imageUrl,
                            contentDescription = category.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Category,
                            contentDescription = category.name,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(Dimens.CategoryIconSize)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Category Name
                Text(
                    text = category.name,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Normal,
                    fontSize = 10.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun ProductsGrid(
    products: List<ProductDto>,
    onProductClick: (String) -> Unit,
    favoriteIds: Set<Int> = emptySet(),
    onToggleFavorite: (Int) -> Unit = {}
) {
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
                        isFavorite = favoriteIds.contains(product.id),
                        onFavoriteClick = { onToggleFavorite(product.id) },
                        onClick = { onProductClick(product.slug) },
                        modifier = Modifier.weight(1f)
                    )
                }
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
