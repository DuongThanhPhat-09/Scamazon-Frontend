package com.example.scamazon_frontend.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.scamazon_frontend.core.utils.Resource
import com.example.scamazon_frontend.data.models.product.ProductDto
import com.example.scamazon_frontend.core.utils.formatPrice
import com.example.scamazon_frontend.di.ViewModelFactory
import com.example.scamazon_frontend.ui.components.*
import com.example.scamazon_frontend.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    viewModel: SearchViewModel = viewModel(factory = ViewModelFactory(LocalContext.current)),
    onProductClick: (String) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val productsState by viewModel.products.collectAsStateWithLifecycle()
    val sortBy by viewModel.sortBy.collectAsStateWithLifecycle()
    var showSortSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
    ) {
        // Top Bar with Search
        LafyuuTopAppBar(title = "Explore", onBackClick = onNavigateBack)

        // Search Bar
        LafyuuTextField(
            value = searchQuery,
            onValueChange = { viewModel.onSearchQueryChange(it) },
            placeholder = "Search products...",
            leadingIcon = Icons.Default.Search,
            modifier = Modifier.padding(horizontal = Dimens.ScreenPadding, vertical = 8.dp)
        )

        // Sort & Filter Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.ScreenPadding, vertical = 4.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChip(
                selected = false,
                onClick = { showSortSheet = true },
                label = {
                    Text(
                        text = when (sortBy) {
                            "price" -> "Price"
                            "name" -> "Name"
                            "rating" -> "Rating"
                            else -> "Newest"
                        },
                        style = Typography.bodySmall
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Sort,
                        contentDescription = "Sort",
                        modifier = Modifier.size(16.dp)
                    )
                }
            )
        }

        // Products Grid
        when (productsState) {
            is Resource.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            }
            is Resource.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = productsState.message ?: "Error loading products",
                            style = Typography.bodyLarge,
                            color = StatusError
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        LafyuuPrimaryButton(
                            text = "Retry",
                            onClick = { viewModel.searchProducts() },
                            modifier = Modifier.width(200.dp)
                        )
                    }
                }
            }
            is Resource.Success -> {
                val products = productsState.data!!
                if (products.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        EmptyState(
                            title = "No Products Found",
                            message = "Try searching with different keywords"
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(Dimens.ScreenPadding),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(products) { product ->
                            SearchProductCard(
                                product = product,
                                onClick = { onProductClick(product.slug) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Sort Bottom Sheet
    if (showSortSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSortSheet = false }
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Sort By", style = Typography.titleLarge, color = TextPrimary)
                Spacer(modifier = Modifier.height(16.dp))

                val sortOptions = listOf(
                    "newest" to "Newest",
                    "price" to "Price",
                    "name" to "Name",
                    "rating" to "Rating"
                )

                sortOptions.forEach { (value, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.onSortChanged(value)
                                showSortSheet = false
                            }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = sortBy == value,
                            onClick = {
                                viewModel.onSortChanged(value)
                                showSortSheet = false
                            },
                            colors = RadioButtonDefaults.colors(selectedColor = PrimaryBlue)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = label, style = Typography.bodyLarge, color = TextPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun SearchProductCard(
    product: ProductDto,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = LafyuuShapes.CardShape,
        colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Product Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(BackgroundLight)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(product.primaryImage)
                        .crossfade(true)
                        .build(),
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // Product Info
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = product.name,
                    style = Typography.titleSmall,
                    color = TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Rating
                product.avgRating?.let { rating ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = SecondaryYellow,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = String.format("%.1f", rating),
                            style = Typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                // Price
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val displayPrice = product.salePrice ?: product.price
                    Text(
                        text = "${formatPrice(displayPrice)}đ",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = PrimaryBlue
                    )

                    if (product.salePrice != null) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${formatPrice(product.price)}đ",
                            fontFamily = Poppins,
                            fontSize = 10.sp,
                            color = TextHint,
                            textDecoration = TextDecoration.LineThrough
                        )
                    }
                }
            }
        }
    }
}
