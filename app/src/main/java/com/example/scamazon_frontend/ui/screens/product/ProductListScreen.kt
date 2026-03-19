package com.example.scamazon_frontend.ui.screens.product

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.scamazon_frontend.core.utils.Resource
import com.example.scamazon_frontend.data.models.admin.BrandDto
import com.example.scamazon_frontend.data.models.category.CategoryDto
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
    val currentFilter by viewModel.currentFilter.collectAsStateWithLifecycle()
    val currentPage by viewModel.currentPage.collectAsStateWithLifecycle()
    val totalPages by viewModel.totalPages.collectAsStateWithLifecycle()
    val brands by viewModel.brands.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()

    var showSortSheet by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }
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

    if (showFilterSheet) {
        FilterBottomSheet(
            currentFilter = currentFilter,
            brands = brands,
            categories = categories,
            onApply = { filter ->
                viewModel.setFilter(filter)
                showFilterSheet = false
            },
            onDismiss = { showFilterSheet = false }
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

        // Sort / Filter Bar
        SortFilterBar(
            currentSort = currentSort,
            activeFilterCount = currentFilter.activeCount,
            isGridView = isGridView,
            onSortClick = { showSortSheet = true },
            onFilterClick = { showFilterSheet = true },
            onViewToggle = { isGridView = !isGridView }
        )

        // Active filter chips
        if (!currentFilter.isEmpty) {
            ActiveFilterChips(
                filter = currentFilter,
                brands = brands,
                categories = categories,
                onClearAll = { viewModel.clearFilter() }
            )
        }

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
                                    onClick = { onNavigateToProductDetail(product.slug) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            } else {
                                ProductCardHorizontal(
                                    productName = product.name,
                                    productImage = product.primaryImage ?: "",
                                    price = product.salePrice ?: product.price,
                                    quantity = product.soldCount ?: 0,
                                    onClick = { onNavigateToProductDetail(product.slug) }
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
    activeFilterCount: Int,
    isGridView: Boolean,
    onSortClick: () -> Unit,
    onFilterClick: () -> Unit,
    onViewToggle: () -> Unit
) {
    val sortLabel = when (currentSort) {
        "newest" -> "Newest"
        "price_asc" -> "Price ↑"
        "price_desc" -> "Price ↓"
        "name" -> "Name"
        "rating" -> "Rating"
        else -> "Sort"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
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
                        imageVector = Icons.AutoMirrored.Filled.Sort,
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

            // Filter Button
            BadgedBox(
                badge = {
                    if (activeFilterCount > 0) {
                        Badge(containerColor = SecondaryRed) {
                            Text(
                                text = activeFilterCount.toString(),
                                color = White,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            ) {
                Surface(
                    modifier = Modifier.clickable { onFilterClick() },
                    shape = LafyuuShapes.ChipShape,
                    color = if (activeFilterCount > 0) SecondaryRed.copy(alpha = 0.1f) else BackgroundLight
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter",
                            tint = if (activeFilterCount > 0) SecondaryRed else TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Filter",
                            fontFamily = Poppins,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (activeFilterCount > 0) SecondaryRed else TextSecondary
                        )
                    }
                }
            }
        }

        // View Toggle
        IconButton(onClick = onViewToggle) {
            Icon(
                imageVector = if (isGridView) Icons.AutoMirrored.Filled.ViewList else Icons.Default.GridView,
                contentDescription = if (isGridView) "List View" else "Grid View",
                tint = PrimaryBlue
            )
        }
    }
}

@Composable
private fun ActiveFilterChips(
    filter: ProductFilter,
    brands: List<BrandDto>,
    categories: List<CategoryDto>,
    onClearAll: () -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filter.categoryId?.let { cid ->
            val catName = categories.find { it.id == cid }?.name ?: "Category"
            item {
                FilterActiveChip(label = catName)
            }
        }
        filter.brandId?.let { bid ->
            val brandName = brands.find { it.id == bid }?.name ?: "Brand"
            item {
                FilterActiveChip(label = brandName)
            }
        }
        filter.minPrice?.let { min ->
            item {
                FilterActiveChip(label = "From ${min.toLong()}đ")
            }
        }
        filter.maxPrice?.let { max ->
            item {
                FilterActiveChip(label = "To ${max.toLong()}đ")
            }
        }
        filter.minRating?.let { rating ->
            item {
                FilterActiveChip(label = "$rating★+")
            }
        }
        item {
            Surface(
                modifier = Modifier.clickable { onClearAll() },
                shape = LafyuuShapes.ChipShape,
                color = SecondaryRed.copy(alpha = 0.1f)
            ) {
                Text(
                    text = "Clear all",
                    fontFamily = Poppins,
                    fontSize = 11.sp,
                    color = SecondaryRed,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun FilterActiveChip(label: String) {
    Surface(
        shape = LafyuuShapes.ChipShape,
        color = PrimaryBlueSoft
    ) {
        Text(
            text = label,
            fontFamily = Poppins,
            fontSize = 11.sp,
            color = PrimaryBlue,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

// ─── Sort Bottom Sheet ────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SortBottomSheet(
    currentSort: String,
    onSortSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val sortOptions = listOf(
        "newest" to "Newest",
        "price_asc" to "Price: Low to High",
        "price_desc" to "Price: High to Low",
        "name" to "Name",
        "rating" to "Rating"
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterBottomSheet(
    currentFilter: ProductFilter,
    brands: List<BrandDto>,
    categories: List<CategoryDto>,
    onApply: (ProductFilter) -> Unit,
    onDismiss: () -> Unit
) {
    // Local draft state — only applied on "Apply"
    var selectedBrandId by remember { mutableStateOf(currentFilter.brandId) }
    var selectedCategoryId by remember { mutableStateOf(currentFilter.categoryId) }
    var minPriceText by remember { mutableStateOf(currentFilter.minPrice?.toLong()?.toString() ?: "") }
    var maxPriceText by remember { mutableStateOf(currentFilter.maxPrice?.toLong()?.toString() ?: "") }
    var selectedRating by remember { mutableStateOf(currentFilter.minRating) }

    val ratingOptions = listOf(null to "Any", 3 to "3★+", 4 to "4★+", 5 to "5★")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = White,
        shape = LafyuuShapes.BottomSheetShape
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filter",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextPrimary
                )
                TextButton(onClick = {
                    selectedBrandId = null
                    selectedCategoryId = null
                    minPriceText = ""
                    maxPriceText = ""
                    selectedRating = null
                }) {
                    Text("Reset", color = SecondaryRed, fontFamily = Poppins)
                }
            }

            // Category Section
            if (categories.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Category",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(categories) { category ->
                            val isSelected = selectedCategoryId == category.id
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedCategoryId = if (isSelected) null else category.id
                                },
                                label = {
                                    Text(
                                        text = category.name,
                                        fontFamily = Poppins,
                                        fontSize = 12.sp
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PrimaryBlueSoft,
                                    selectedLabelColor = PrimaryBlue
                                )
                            )
                        }
                    }
                }
            }

            // Brand Section
            if (brands.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Brand",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(brands) { brand ->
                            val isSelected = selectedBrandId == brand.id
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedBrandId = if (isSelected) null else brand.id
                                },
                                label = {
                                    Text(
                                        text = brand.name,
                                        fontFamily = Poppins,
                                        fontSize = 12.sp
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PrimaryBlueSoft,
                                    selectedLabelColor = PrimaryBlue
                                )
                            )
                        }
                    }
                }
            }

            // Price Range Section
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Price Range (đ)",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = TextPrimary
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = minPriceText,
                        onValueChange = { minPriceText = it.filter { c -> c.isDigit() } },
                        placeholder = { Text("Min", fontFamily = Poppins, fontSize = 13.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = BorderLight
                        )
                    )
                    Text("–", color = TextSecondary)
                    OutlinedTextField(
                        value = maxPriceText,
                        onValueChange = { maxPriceText = it.filter { c -> c.isDigit() } },
                        placeholder = { Text("Max", fontFamily = Poppins, fontSize = 13.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = BorderLight
                        )
                    )
                }
            }

            // Min Rating Section
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Min Rating",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = TextPrimary
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ratingOptions.forEach { (value, label) ->
                        val isSelected = selectedRating == value
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedRating = if (isSelected && value != null) null else value },
                            label = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    if (value != null) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            modifier = Modifier.size(12.dp),
                                            tint = if (isSelected) PrimaryBlue else TextHint
                                        )
                                    }
                                    Text(text = label, fontFamily = Poppins, fontSize = 12.sp)
                                }
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryBlueSoft,
                                selectedLabelColor = PrimaryBlue
                            )
                        )
                    }
                }
            }

            // Apply Button
            LafyuuPrimaryButton(
                text = "Apply Filter",
                onClick = {
                    onApply(
                        ProductFilter(
                            brandId = selectedBrandId,
                            categoryId = selectedCategoryId,
                            minPrice = minPriceText.toLongOrNull()?.toDouble(),
                            maxPrice = maxPriceText.toLongOrNull()?.toDouble(),
                            minRating = selectedRating
                        )
                    )
                }
            )
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
