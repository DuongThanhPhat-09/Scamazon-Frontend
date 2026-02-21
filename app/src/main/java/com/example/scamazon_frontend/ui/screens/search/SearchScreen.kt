package com.example.scamazon_frontend.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scamazon_frontend.ui.theme.*

@Composable
fun SearchScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToSearchResult: (String) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    val recentSearches = remember {
        mutableStateListOf("Nike Air Max", "Adidas Ultraboost", "Running Shoes", "Sneakers")
    }

    val trendingSearches = listOf(
        "Summer Collection", "Flash Sale", "New Arrivals",
        "Sports Wear", "Casual Shoes", "Leather Bag"
    )

    val suggestedCategories = listOf(
        "Shoes", "Clothing", "Bags", "Accessories", "Electronics", "Sports"
    )

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
    ) {
        // Search Bar Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = PrimaryBlue
                )
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester),
                placeholder = {
                    Text(
                        text = "Search products...",
                        fontFamily = Poppins,
                        color = TextHint,
                        fontSize = 14.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = TextSecondary
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = TextSecondary
                            )
                        }
                    }
                },
                singleLine = true,
                shape = LafyuuShapes.SearchBarShape,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = BorderDefault,
                    focusedContainerColor = White,
                    unfocusedContainerColor = White,
                    cursorColor = PrimaryBlue
                ),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                    onSearch = {
                        if (searchQuery.isNotBlank()) {
                            if (!recentSearches.contains(searchQuery)) {
                                recentSearches.add(0, searchQuery)
                            }
                            onNavigateToSearchResult(searchQuery)
                        }
                    }
                ),
                textStyle = Typography.bodyLarge.copy(color = TextPrimary)
            )

            if (searchQuery.isNotEmpty()) {
                TextButton(
                    onClick = {
                        if (searchQuery.isNotBlank()) {
                            if (!recentSearches.contains(searchQuery)) {
                                recentSearches.add(0, searchQuery)
                            }
                            onNavigateToSearchResult(searchQuery)
                        }
                    }
                ) {
                    Text(
                        text = "Search",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryBlue,
                        fontSize = 14.sp
                    )
                }
            }
        }

        HorizontalDivider(color = BorderLight)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Recent Searches
            if (recentSearches.isNotEmpty()) {
                item {
                    SearchSection(
                        title = "Recent Searches",
                        onClearAll = { recentSearches.clear() }
                    ) {
                        recentSearches.forEachIndexed { index, query ->
                            RecentSearchItem(
                                query = query,
                                onSearch = { onNavigateToSearchResult(query) },
                                onRemove = { recentSearches.removeAt(index) }
                            )
                            if (index < recentSearches.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    color = BorderLight
                                )
                            }
                        }
                    }
                }
            }

            // Trending Searches
            item {
                SearchSection(title = "Trending Searches") {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(trendingSearches) { trend ->
                            TrendingChip(
                                text = trend,
                                onClick = { onNavigateToSearchResult(trend) }
                            )
                        }
                    }
                }
            }

            // Browse Categories
            item {
                SearchSection(title = "Browse Categories") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        suggestedCategories.chunked(2).forEach { rowItems ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowItems.forEach { category ->
                                    CategoryChip(
                                        text = category,
                                        modifier = Modifier.weight(1f),
                                        onClick = { onNavigateToSearchResult(category) }
                                    )
                                }
                                if (rowItems.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
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
private fun SearchSection(
    title: String,
    onClearAll: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = TextPrimary
            )
            if (onClearAll != null) {
                TextButton(onClick = onClearAll) {
                    Text(
                        text = "Clear All",
                        fontFamily = Poppins,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }
        content()
    }
}

@Composable
private fun RecentSearchItem(
    query: String,
    onSearch: () -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSearch() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.History,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = query,
            style = Typography.bodyMedium,
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )
        IconButton(
            onClick = onRemove,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove",
                tint = TextHint,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun TrendingChip(
    text: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = LafyuuShapes.ChipShape,
        color = PrimaryBlueSoft
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.TrendingUp,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = text,
                fontFamily = Poppins,
                fontSize = 12.sp,
                color = PrimaryBlue
            )
        }
    }
}

@Composable
private fun CategoryChip(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = LafyuuShapes.CardShape,
        color = BackgroundLight,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontFamily = Poppins,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                color = TextPrimary
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SearchScreenPreview() {
    ScamazonFrontendTheme {
        SearchScreen()
    }
}
