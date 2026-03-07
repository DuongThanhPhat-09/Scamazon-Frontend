package com.example.scamazon_frontend.ui.screens.order

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.scamazon_frontend.core.utils.Resource
import com.example.scamazon_frontend.core.utils.formatPrice
import com.example.scamazon_frontend.data.models.order.OrderSummaryDto
import com.example.scamazon_frontend.di.ViewModelFactory
import com.example.scamazon_frontend.ui.components.*
import com.example.scamazon_frontend.ui.theme.*

@Composable
fun OrderHistoryScreen(
    viewModel: OrderHistoryViewModel = viewModel(factory = ViewModelFactory(LocalContext.current)),
    onNavigateBack: () -> Unit = {},
    onOrderClick: (String) -> Unit = {},
    onContinuePayment: (String) -> Unit = {}
) {
    val ordersState by viewModel.ordersState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
    ) {
        LafyuuTopAppBar(title = "My Orders", onBackClick = onNavigateBack)

        when (ordersState) {
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
                            text = ordersState.message ?: "Error loading orders",
                            style = Typography.bodyLarge,
                            color = StatusError
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        LafyuuPrimaryButton(
                            text = "Retry",
                            onClick = { viewModel.fetchOrders() },
                            modifier = Modifier.width(200.dp)
                        )
                    }
                }
            }
            is Resource.Success -> {
                val orders = ordersState.data!!
                if (orders.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        EmptyState(
                            title = "No Orders Yet",
                            message = "Your order history will appear here"
                        ) {
                            LafyuuPrimaryButton(
                                text = "Start Shopping",
                                onClick = onNavigateBack,
                                modifier = Modifier.width(200.dp)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(Dimens.ScreenPadding),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(orders, key = { it.id }) { order ->
                            val isPending = order.status?.lowercase() == "pending"
                            OrderCard(
                                order = order,
                                isPending = isPending,
                                onDetailClick = { onOrderClick(order.id.toString()) },
                                onContinuePayment = { onContinuePayment(order.id.toString()) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderCard(
    order: OrderSummaryDto,
    isPending: Boolean,
    onDetailClick: () -> Unit,
    onContinuePayment: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = LafyuuShapes.CardShape,
        colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Order Code + Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = order.orderCode,
                    style = Typography.titleMedium,
                    color = TextPrimary
                )

                OrderStatusBadge(status = order.status ?: "pending")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Order Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Product Image
                if (!order.firstProductImage.isNullOrEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(order.firstProductImage)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Order Product",
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(BackgroundLight),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${order.itemCount} item${if (order.itemCount > 1) "s" else ""}",
                        style = Typography.bodyMedium,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${formatPrice(order.total)}đ",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = PrimaryBlue
                    )
                }

                // Date
                order.createdAt?.let { date ->
                    Text(
                        text = date.take(10), // yyyy-MM-dd
                        style = Typography.bodySmall,
                        color = TextHint
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isPending) {
                    // "Continue Payment" button for pending orders
                    Button(
                        onClick = onContinuePayment,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBlue
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Payment,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Tiếp tục thanh toán",
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            color = White
                        )
                    }
                } else {
                    // "Detail" button for non-pending orders
                    OutlinedButton(
                        onClick = onDetailClick,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = PrimaryBlue
                        ),
                        border = BorderStroke(1.dp, PrimaryBlue),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Chi tiết",
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderStatusBadge(status: String) {
    val (bgColor, textColor, label) = when (status.lowercase()) {
        "pending" -> Triple(SecondaryYellow.copy(alpha = 0.15f), SecondaryYellow, "Pending")
        "confirmed" -> Triple(PrimaryBlue.copy(alpha = 0.15f), PrimaryBlue, "Confirmed")
        "shipping" -> Triple(PrimaryBlue.copy(alpha = 0.15f), PrimaryBlue, "Shipping")
        "delivered" -> Triple(StatusSuccess.copy(alpha = 0.15f), StatusSuccess, "Delivered")
        "cancelled" -> Triple(StatusError.copy(alpha = 0.15f), StatusError, "Cancelled")
        else -> Triple(BackgroundLight, TextSecondary, status.replaceFirstChar { it.uppercase() })
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = bgColor
    ) {
        Text(
            text = label,
            style = Typography.bodySmall,
            color = textColor,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}
