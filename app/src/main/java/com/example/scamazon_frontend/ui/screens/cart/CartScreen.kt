package com.example.scamazon_frontend.ui.screens.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.scamazon_frontend.core.utils.Resource
import com.example.scamazon_frontend.core.utils.formatPrice
import com.example.scamazon_frontend.data.models.cart.CartItemDto
import com.example.scamazon_frontend.di.ViewModelFactory
import com.example.scamazon_frontend.ui.components.*
import com.example.scamazon_frontend.ui.theme.*

@Composable
fun CartScreen(
    viewModel: CartViewModel = viewModel(factory = ViewModelFactory(LocalContext.current)),
    onNavigateToCheckout: () -> Unit = {},
    onNavigateToProductDetail: (String) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val cartState by viewModel.cartState.collectAsStateWithLifecycle()
    val operationMessage by viewModel.operationMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    // Show operation messages
    LaunchedEffect(operationMessage) {
        operationMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
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
            // Top App Bar
            LafyuuTopAppBar(
                title = "Your Cart",
                onBackClick = onNavigateBack
            )

            when (cartState) {
                is Resource.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryBlue)
                    }
                }
                is Resource.Error -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = cartState.message ?: "Error loading cart",
                                style = Typography.bodyLarge,
                                color = StatusError
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            LafyuuPrimaryButton(
                                text = "Retry",
                                onClick = { viewModel.fetchCart() },
                                modifier = Modifier.width(200.dp)
                            )
                        }
                    }
                }
                is Resource.Success -> {
                    val cart = cartState.data!!
                    if (cart.items.isEmpty()) {
                        // Empty Cart State
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            EmptyState(
                                title = "Your Cart is Empty",
                                message = "Looks like you haven't added anything to your cart yet"
                            ) {
                                LafyuuPrimaryButton(
                                    text = "Start Shopping",
                                    onClick = onNavigateBack,
                                    modifier = Modifier.width(200.dp)
                                )
                            }
                        }
                    } else {
                        // Cart Items List
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(Dimens.ScreenPadding),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(cart.items, key = { it.id }) { item ->
                                CartItemCard(
                                    item = item,
                                    onQuantityIncrease = {
                                        if (item.quantity < item.stockQuantity) {
                                            viewModel.updateQuantity(item.id, item.quantity + 1)
                                        }
                                    },
                                    onQuantityDecrease = {
                                        if (item.quantity > 1) {
                                            viewModel.updateQuantity(item.id, item.quantity - 1)
                                        }
                                    },
                                    onRemove = {
                                        viewModel.removeItem(item.id)
                                    },
                                    onClick = { onNavigateToProductDetail(item.productId.toString()) }
                                )
                            }
                        }

                        // Bottom Section - Total & Checkout
                        CartBottomSection(
                            subtotal = cart.subtotal,
                            itemCount = cart.totalItems,
                            onCheckout = onNavigateToCheckout
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CartItemCard(
    item: CartItemDto,
    onQuantityIncrease: () -> Unit,
    onQuantityDecrease: () -> Unit,
    onRemove: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = LafyuuShapes.CardShape,
        colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Product Image
            if (!item.productImage.isNullOrEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(item.productImage)
                        .crossfade(true)
                        .build(),
                    contentDescription = item.productName,
                    modifier = Modifier
                        .size(Dimens.ProductImageSize)
                        .background(BackgroundLight, LafyuuShapes.ImageShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(Dimens.ProductImageSize)
                        .background(BackgroundLight, LafyuuShapes.ImageShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Img", style = Typography.bodySmall, color = TextHint)
                }
            }

            // Product Info
            Column(modifier = Modifier.weight(1f)) {
                // Name & Remove button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = item.productName,
                        style = Typography.titleMedium,
                        color = TextPrimary,
                        maxLines = 2,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = onRemove,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Remove",
                            tint = TextHint,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Price & Quantity
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val displayPrice = item.salePrice ?: item.price
                    Text(
                        text = "${formatPrice(displayPrice)}đ",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = PrimaryBlue
                    )

                    CompactQuantitySelector(
                        quantity = item.quantity,
                        onIncrease = onQuantityIncrease,
                        onDecrease = onQuantityDecrease
                    )
                }
            }
        }
    }
}

@Composable
private fun CartBottomSection(
    subtotal: Double,
    itemCount: Int,
    onCheckout: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        color = White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.ScreenPadding)
        ) {
            // Price Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Items ($itemCount)",
                    style = Typography.bodyLarge,
                    color = TextSecondary
                )
                Text(
                    text = "${formatPrice(subtotal)}đ",
                    style = Typography.bodyLarge,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Shipping",
                    style = Typography.bodyLarge,
                    color = TextSecondary
                )
                Text(
                    text = "$40.00",
                    style = Typography.bodyLarge,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            HorizontalDivider(color = BorderLight)

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total Price",
                    style = Typography.titleLarge,
                    color = TextPrimary
                )
                Text(
                    text = "${formatPrice(subtotal + 40.0)}đ",
                    style = Typography.titleLarge,
                    color = PrimaryBlue
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LafyuuPrimaryButton(
                text = "Check Out",
                onClick = onCheckout
            )
        }
    }
}
