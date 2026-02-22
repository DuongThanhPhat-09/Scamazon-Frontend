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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.scamazon_frontend.core.utils.Resource
import com.example.scamazon_frontend.core.utils.formatPrice
import com.example.scamazon_frontend.di.ViewModelFactory
import com.example.scamazon_frontend.ui.components.*
import com.example.scamazon_frontend.ui.screens.favorite.FavoriteViewModel
import com.example.scamazon_frontend.ui.theme.*

@Composable
fun ProductDetailScreen(
    productId: String,
    viewModel: ProductDetailViewModel = viewModel(factory = ViewModelFactory(LocalContext.current)),
    favoriteViewModel: FavoriteViewModel = viewModel(factory = ViewModelFactory(LocalContext.current)),
    onNavigateBack: () -> Unit = {},
    onNavigateToCart: () -> Unit = {},
    onNavigateToReview: (Int) -> Unit = {}
) {
    var quantity by remember { mutableStateOf(1) }

    val productState by viewModel.productState.collectAsStateWithLifecycle()
    val addToCartState by viewModel.addToCartState.collectAsStateWithLifecycle()
    val favoriteIds by favoriteViewModel.favoriteIds.collectAsStateWithLifecycle()

    // Load product on first composition
    LaunchedEffect(productId) {
        viewModel.loadProduct(productId)
    }

    // Handle add to cart result
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(addToCartState) {
        when (addToCartState) {
            is Resource.Success -> {
                snackbarHostState.showSnackbar("Đã thêm vào giỏ hàng!")
                viewModel.resetAddToCartState()
            }
            is Resource.Error -> {
                snackbarHostState.showSnackbar(addToCartState?.message ?: "Lỗi thêm giỏ hàng")
                viewModel.resetAddToCartState()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BackgroundWhite)
        ) {
            when (productState) {
                is Resource.Loading -> {
                    LafyuuTopAppBar(title = "Product Detail", onBackClick = onNavigateBack)
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryBlue)
                    }
                }
                is Resource.Error -> {
                    LafyuuTopAppBar(title = "Product Detail", onBackClick = onNavigateBack)
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = productState.message ?: "Error loading product",
                                style = Typography.bodyLarge,
                                color = StatusError
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            LafyuuPrimaryButton(
                                text = "Retry",
                                onClick = { viewModel.loadProduct(productId) },
                                modifier = Modifier.width(200.dp)
                            )
                        }
                    }
                }
                is Resource.Success -> {
                    val product = productState.data!!

                    // Top App Bar
                    LafyuuCartAppBar(
                        title = product.name,
                        onBackClick = onNavigateBack,
                        cartItemCount = 0,
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
                            images = product.images,
                            isFavorite = favoriteIds.contains(product.id),
                            onFavoriteClick = { favoriteViewModel.toggleFavorite(product.id) }
                        )

                        // Product Info
                        Column(
                            modifier = Modifier.padding(Dimens.ScreenPadding)
                        ) {
                            // Name
                            Text(
                                text = product.name,
                                style = Typography.headlineMedium,
                                color = TextPrimary
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Rating
                            val rating = product.ratingSummary?.avgRating ?: 0f
                            val reviewCount = product.ratingSummary?.totalReviews ?: 0
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                RatingBar(rating = rating)
                                Text(
                                    text = "($reviewCount Reviews)",
                                    style = Typography.bodySmall,
                                    color = TextSecondary,
                                    modifier = Modifier.clickable { onNavigateToReview(product.id) }
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Price
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val displayPrice = product.salePrice ?: product.price
                                Text(
                                    text = "${formatPrice(displayPrice)}đ",
                                    fontFamily = Poppins,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = PrimaryBlue
                                )

                                if (product.salePrice != null) {
                                    Text(
                                        text = "${formatPrice(product.price)}đ",
                                        fontFamily = Poppins,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 14.sp,
                                        color = TextHint,
                                        textDecoration = TextDecoration.LineThrough
                                    )
                                }

                                product.discountPercent?.let {
                                    if (it > 0) {
                                        Text(
                                            text = "$it% Off",
                                            fontFamily = Poppins,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = SecondaryRed
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Stock Status
                            product.stockStatus?.let { status ->
                                Text(
                                    text = "Stock: $status",
                                    style = Typography.bodyMedium,
                                    color = if (status == "in_stock") StatusSuccess else StatusError
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }

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
                                    onIncrease = {
                                        val maxQty = product.stockQuantity ?: 99
                                        if (quantity < maxQty) quantity++
                                    },
                                    onDecrease = { if (quantity > 1) quantity-- }
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Specifications
                            product.specifications?.let { specs ->
                                if (specs.isNotEmpty()) {
                                    Text(
                                        text = "Specification",
                                        style = Typography.titleMedium,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    specs.forEach { (key, value) ->
                                        SpecificationRow(key, value)
                                    }
                                    Spacer(modifier = Modifier.height(24.dp))
                                }
                            }

                            // Description
                            product.description?.let { desc ->
                                Text(
                                    text = "Description",
                                    style = Typography.titleMedium,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = desc,
                                    style = Typography.bodyLarge,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                            }

                            // Review Section Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Review ($reviewCount)",
                                    style = Typography.titleMedium,
                                    color = TextPrimary
                                )
                                LafyuuTextButton(
                                    text = "See All",
                                    onClick = { onNavigateToReview(product.id) }
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
                        val isAddingToCart = addToCartState is Resource.Loading
                        LafyuuPrimaryButton(
                            text = if (isAddingToCart) "Adding..." else "Add To Cart",
                            onClick = {
                                viewModel.addToCart(product.id, quantity)
                            },
                            enabled = !isAddingToCart,
                            modifier = Modifier.padding(Dimens.ScreenPadding)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductImageSection(
    images: List<com.example.scamazon_frontend.data.models.product.ProductImageDto>,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit
) {
    val primaryImage = images.firstOrNull { it.isPrimary == true } ?: images.firstOrNull()
    var selectedIndex by remember { mutableStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(238.dp)
            .background(BackgroundLight)
    ) {
        // Main Image
        if (images.isNotEmpty()) {
            val currentImage = images.getOrNull(selectedIndex) ?: images.first()
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(currentImage.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Product Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No Image", style = Typography.bodyLarge, color = TextHint)
            }
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
        if (images.size > 1) {
            LazyRow(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(images) { image ->
                    val index = images.indexOf(image)
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                if (index == selectedIndex) PrimaryBlueSoft else White,
                                LafyuuShapes.ImageShape
                            )
                            .border(
                                width = if (index == selectedIndex) 2.dp else 1.dp,
                                color = if (index == selectedIndex) PrimaryBlue else BorderLight,
                                shape = LafyuuShapes.ImageShape
                            )
                            .clickable { selectedIndex = index },
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(image.imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Thumbnail ${index + 1}",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(LafyuuShapes.ImageShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
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
