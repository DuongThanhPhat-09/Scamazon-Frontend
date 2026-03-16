package com.example.scamazon_frontend.ui.screens.search

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
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
import com.example.scamazon_frontend.core.utils.formatPrice
import com.example.scamazon_frontend.data.models.product.ProductDto
import com.example.scamazon_frontend.di.ViewModelFactory
import com.example.scamazon_frontend.ui.components.*
import com.example.scamazon_frontend.ui.theme.*
import kotlinx.coroutines.delay

private val CARD_HEIGHT = 230.dp
private val CARD_IMAGE_HEIGHT = 140.dp

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

    // Entrance animation
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
        delay(300)
        focusRequester.requestFocus()
    }

    val searchBarOffsetY by animateDpAsState(
        targetValue = if (isVisible) 0.dp else (-16).dp,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "searchBarOffset"
    )
    val searchBarAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(300),
        label = "searchBarAlpha"
    )
    val contentAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(350, delayMillis = 100),
        label = "contentAlpha"
    )

    // Product count
    val productCount = (productsState as? Resource.Success)?.data?.size ?: 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
    ) {
        LafyuuTopAppBar(title = "Explore", onBackClick = onNavigateBack)

        // Search Bar
        LafyuuSearchField(
            value = searchQuery,
            onValueChange = { viewModel.onSearchQueryChange(it) },
            placeholder = "Search products...",
            onSearch = { keyboardController?.hide() },
            modifier = Modifier
                .padding(horizontal = Dimens.ScreenPadding, vertical = 8.dp)
                .offset(y = searchBarOffsetY)
                .alpha(searchBarAlpha)
                .focusRequester(focusRequester)
        )

        // Sort bar + result count
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.ScreenPadding, vertical = 4.dp)
                .alpha(contentAlpha),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Result count
            if (productsState is Resource.Success && productCount > 0) {
                Text(
                    text = "$productCount results",
                    fontFamily = Poppins,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }

            // Sort chip
            val sortLabel = when (sortBy) {
                "price" -> "Price ↑"
                "name" -> "Name"
                "rating" -> "Rating"
                else -> "Newest"
            }
            Surface(
                modifier = Modifier.clickable { showSortSheet = true },
                shape = LafyuuShapes.ChipShape,
                color = PrimaryBlueSoft
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Sort,
                        contentDescription = "Sort",
                        tint = PrimaryBlue,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = sortLabel,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = PrimaryBlue
                    )
                }
            }
        }

        HorizontalDivider(color = BorderLight, modifier = Modifier.alpha(contentAlpha))

        // Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(contentAlpha)
        ) {
            when (productsState) {
                is Resource.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        FullScreenLoading()
                    }
                }
                is Resource.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        ErrorState(
                            message = (productsState as Resource.Error).message ?: "Error loading products",
                            onRetry = { viewModel.searchProducts() }
                        )
                    }
                }
                is Resource.Success -> {
                    val products = (productsState as Resource.Success<List<ProductDto>>).data ?: emptyList()
                    if (products.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            EmptyState(
                                title = if (searchQuery.isBlank()) "No Products" else "No results for \"$searchQuery\"",
                                message = "Try searching with different keywords"
                            )
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(Dimens.ScreenPadding),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(products, key = { it.id }) { product ->
                                ExploreProductCard(
                                    product = product,
                                    onClick = { onProductClick(product.slug) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Sort Bottom Sheet
    if (showSortSheet) {
        ExploreSortSheet(
            currentSort = sortBy,
            onSortSelected = {
                viewModel.onSortChanged(it)
                showSortSheet = false
            },
            onDismiss = { showSortSheet = false }
        )
    }
}

// ─── Product Card (fixed height, equal layout) ───────────────────────────────

@Composable
private fun ExploreProductCard(
    product: ProductDto,
    onClick: () -> Unit
) {
    val displayPrice = product.salePrice ?: product.price
    val hasDiscount = product.salePrice != null
    val discountPct = if (hasDiscount && product.price > 0) {
        ((product.price - product.salePrice!!) / product.price * 100).toInt()
    } else null

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(CARD_HEIGHT)
            .clickable { onClick() },
        shape = LafyuuShapes.CardShape,
        colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Image section — fixed height
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(CARD_IMAGE_HEIGHT)
                    .background(BackgroundLight)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(product.primaryImage)
                        .crossfade(true)
                        .build(),
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
                    contentScale = ContentScale.Crop
                )

                // Discount badge
                discountPct?.let {
                    Box(
                        modifier = Modifier
                            .padding(6.dp)
                            .align(Alignment.TopStart)
                            .background(AccentGold, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "-$it%",
                            color = White,
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        )
                    }
                }
            }

            // Info section — fills remaining height, price pushed to bottom
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top: name
                Text(
                    text = product.name,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )

                // Bottom: rating + price
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    // Rating
                    val rating = product.avgRating ?: 0f
                    if (rating > 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = AccentGold,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = String.format("%.1f", rating),
                                fontFamily = Poppins,
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    } else {
                        // Placeholder to keep layout stable when no rating
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // Price row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "${formatPrice(displayPrice)}đ",
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = PrimaryBlue
                        )
                        if (hasDiscount) {
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
}

// ─── Sort Bottom Sheet ────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExploreSortSheet(
    currentSort: String,
    onSortSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val sortOptions = listOf(
        "newest" to "Newest",
        "price"  to "Price: Low to High",
        "rating" to "Highest Rated",
        "name"   to "Name: A–Z"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = White,
        shape = LafyuuShapes.BottomSheetShape
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Sort By",
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            sortOptions.forEach { (value, label) ->
                val isSelected = currentSort == value
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSortSelected(value) },
                    color = if (isSelected) PrimaryBlueSoft else White,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = label,
                            fontFamily = Poppins,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isSelected) PrimaryBlue else TextPrimary
                        )
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
                if (value != sortOptions.last().first) {
                    HorizontalDivider(
                        color = BorderLight,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }
        }
    }
}
