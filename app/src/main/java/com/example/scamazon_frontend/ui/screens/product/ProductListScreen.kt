package com.example.scamazon_frontend.ui.screens.product

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.scamazon_frontend.core.utils.Resource
import com.example.scamazon_frontend.data.models.product.ProductDto
import com.example.scamazon_frontend.di.ViewModelFactory
import com.example.scamazon_frontend.ui.components.*
import com.example.scamazon_frontend.ui.theme.*

@Composable
fun ProductListScreen(
    categoryId: String? = null,
    categoryName: String? = null,
    viewModel: ProductListViewModel = viewModel(factory = ViewModelFactory(LocalContext.current)),
    onNavigateBack: () -> Unit = {},
    onNavigateToProductDetail: (String) -> Unit = {}
) {
    val productsState by viewModel.productsState.collectAsStateWithLifecycle()
    val currentSort by viewModel.currentSort.collectAsStateWithLifecycle()
    val currentPage by viewModel.currentPage.collectAsStateWithLifecycle()
    val totalPages by viewModel.totalPages.collectAsStateWithLifecycle()

    var showSortSheet by remember { mutableStateOf(false) }
    var isGridView by remember { mutableStateOf(true) }

    val gridState = rememberLazyGridState()

    LaunchedEffect(categoryId) {
        viewModel.init(categoryId?.toIntOrNull())
    }

    // Infinite scroll detection
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = gridState.layoutInfo.totalItemsCount
            lastVisibleItem >= totalItems - 4 && currentPage < totalPages
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value && productsState is Resource.Success) {
            viewModel.loadNextPage()
        }
    }

    if (showSortSheet) {
        SortBottomSheet(
            currentSort = currentSort,
            onSortSelected = { sort ->
                viewModel.setSort(sort)
                showSortSheet = false
            },
            onDismiss = { showSortSheet = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
    ) {
        LafyuuTopAppBar(
            title = categoryName ?: "Products",
            onBackClick = onNavigateBack
        )

        // Filter / Sort Bar
        SortFilterBar(
            currentSort = currentSort,
            isGridView = isGridView,
            onSortClick = { showSortSheet = true },
            onViewToggle = { isGridView = !isGridView }
        )

        HorizontalDivider(color = BorderLight)

        // Content
        when (productsState) {
            is Resource.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    FullScreenLoading()
                }
            }
            is Resource.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    ErrorState(
                        message = (productsState as Resource.Error).message ?: "Unknown error",
                        onRetry = { viewModel.refresh() }
                    )
                }
            }
            is Resource.Success -> {
                val products = (productsState as Resource.Success<List<ProductDto>>).data ?: emptyList()
                if (products.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        EmptyState(
                            title = "No Products Found",
                            message = "Try changing your filters or search criteria"
                        )
                    }
                } else {
                    val columns = if (isGridView) 2 else 1

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(columns),
                        state = gridState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(products, key = { it.id }) { product ->
                            if (isGridView) {
                                ProductCard(
                                    productName = product.name,
                                    productImage = product.primaryImage ?: "",
                                    price = product.salePrice ?: product.price,
                                    originalPrice = if (product.salePrice != null) product.price else null,
                                    discount = if (product.salePrice != null && product.price > 0) {
                                        ((product.price - product.salePrice) / product.price * 100).toInt()
                                    } else null,
                                    rating = product.avgRating ?: 0f,
                                    onClick = { onNavigateToProductDetail(product.id.toString()) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            } else {
                                ProductCardHorizontal(
                                    productName = product.name,
                                    productImage = product.primaryImage ?: "",
                                    price = product.salePrice ?: product.price,
                                    quantity = product.soldCount ?: 0,
                                    onClick = { onNavigateToProductDetail(product.id.toString()) }
                                )
                            }
                        }

                        // Loading more indicator
                        if (currentPage < totalPages) {
                            item(span = { GridItemSpan(columns) }) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    InlineLoading(message = "Loading more...")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SortFilterBar(
    currentSort: String,
    isGridView: Boolean,
    onSortClick: () -> Unit,
    onViewToggle: () -> Unit
) {
    val sortLabel = when (currentSort) {
        "newest" -> "Newest"
        "price_asc" -> "Price: Low to High"
        "price_desc" -> "Price: High to Low"
        "popular" -> "Popular"
        "rating" -> "Top Rated"
        else -> "Sort"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Sort Button
        Surface(
            modifier = Modifier.clickable { onSortClick() },
            shape = LafyuuShapes.ChipShape,
            color = PrimaryBlueSoft
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Sort,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = sortLabel,
                    fontFamily = Poppins,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = PrimaryBlue
                )
            }
        }

        // View Toggle
        IconButton(onClick = onViewToggle) {
            Icon(
                imageVector = if (isGridView) Icons.Default.ViewList else Icons.Default.GridView,
                contentDescription = if (isGridView) "List View" else "Grid View",
                tint = PrimaryBlue
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SortBottomSheet(
    currentSort: String,
    onSortSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val sortOptions = listOf(
        "newest" to "Newest",
        "popular" to "Popular",
        "price_asc" to "Price: Low to High",
        "price_desc" to "Price: High to Low",
        "rating" to "Top Rated"
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
                modifier = Modifier.padding(bottom = 16.dp)
            )

            sortOptions.forEach { (value, label) ->
                val isSelected = currentSort == value
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSortSelected(value) },
                    color = if (isSelected) PrimaryBlueSoft else White
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
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
                                imageVector = Icons.Default.FilterList,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
                if (value != sortOptions.last().first) {
                    HorizontalDivider(color = BorderLight)
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProductListScreenPreview() {
    ScamazonFrontendTheme {
        ProductListScreen()
    }
}
