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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scamazon_frontend.ui.components.*
import com.example.scamazon_frontend.ui.theme.*

@Composable
fun CartScreen(
    onNavigateToCheckout: () -> Unit = {},
    onNavigateToProductDetail: (String) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    // Sample cart items
    var cartItems by remember {
        mutableStateOf(
            listOf(
                CartItem("1", "Nike Air Zoom Pegasus 36 Miami", "", 299.43, 2),
                CartItem("2", "Nike Air Max 270 React ENG", "", 199.99, 1),
                CartItem("3", "Adidas Ultraboost 21", "", 249.99, 1)
            )
        )
    }

    val totalPrice = cartItems.sumOf { it.price * it.quantity }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
    ) {
        // Top App Bar
        LafyuuTopAppBar(
            title = "Your Cart",
            onBackClick = onNavigateBack
        )

        if (cartItems.isEmpty()) {
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
                items(cartItems, key = { it.id }) { item ->
                    CartItemCard(
                        item = item,
                        onQuantityIncrease = {
                            cartItems = cartItems.map {
                                if (it.id == item.id) it.copy(quantity = it.quantity + 1)
                                else it
                            }
                        },
                        onQuantityDecrease = {
                            if (item.quantity > 1) {
                                cartItems = cartItems.map {
                                    if (it.id == item.id) it.copy(quantity = it.quantity - 1)
                                    else it
                                }
                            }
                        },
                        onRemove = {
                            cartItems = cartItems.filter { it.id != item.id }
                        },
                        onClick = { onNavigateToProductDetail(item.id) }
                    )
                }

                // Coupon Section
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    CouponSection()
                }
            }

            // Bottom Section - Total & Checkout
            CartBottomSection(
                totalPrice = totalPrice,
                itemCount = cartItems.size,
                onCheckout = onNavigateToCheckout
            )
        }
    }
}

@Composable
private fun CartItemCard(
    item: CartItem,
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
            Box(
                modifier = Modifier
                    .size(Dimens.ProductImageSize)
                    .background(BackgroundLight, LafyuuShapes.ImageShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Img",
                    style = Typography.bodySmall,
                    color = TextHint
                )
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
                        text = item.name,
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
                    Text(
                        text = "$${String.format("%.2f", item.price)}",
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
private fun CouponSection() {
    var couponCode by remember { mutableStateOf("") }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = couponCode,
            onValueChange = { couponCode = it },
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            placeholder = {
                Text(
                    text = "Enter Coupon Code",
                    style = Typography.bodyMedium,
                    color = TextHint
                )
            },
            shape = LafyuuShapes.InputFieldShape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = BorderLight
            ),
            singleLine = true,
            textStyle = Typography.bodyMedium.copy(color = TextPrimary)
        )

        LafyuuSmallButton(
            text = "Apply",
            onClick = { /* Apply coupon */ }
        )
    }
}

@Composable
private fun CartBottomSection(
    totalPrice: Double,
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
                    text = "$${String.format("%.2f", totalPrice)}",
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
                    text = "$${String.format("%.2f", totalPrice + 40.0)}",
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

// Data class for cart item
private data class CartItem(
    val id: String,
    val name: String,
    val image: String,
    val price: Double,
    val quantity: Int
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CartScreenPreview() {
    ScamazonFrontendTheme {
        CartScreen()
    }
}
