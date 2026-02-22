package com.example.scamazon_frontend.ui.screens.admin.order

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
import com.example.scamazon_frontend.data.models.order.OrderDetailDataDto
import com.example.scamazon_frontend.data.models.order.OrderItemDto
import com.example.scamazon_frontend.di.ViewModelFactory
import com.example.scamazon_frontend.ui.components.*
import com.example.scamazon_frontend.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrderDetailScreen(
    orderId: Int,
    viewModel: AdminOrderViewModel = viewModel(factory = ViewModelFactory(LocalContext.current)),
    onNavigateBack: () -> Unit = {}
) {
    val orderDetailState by viewModel.orderDetailState.collectAsStateWithLifecycle()
    val updateStatusState by viewModel.updateStatusState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(orderId) {
        viewModel.fetchOrderDetail(orderId)
    }

    // Handle update status result
    LaunchedEffect(updateStatusState) {
        when (updateStatusState) {
            is Resource.Success -> {
                snackbarHostState.showSnackbar("Order status updated successfully!")
                viewModel.resetUpdateState()
                viewModel.fetchOrderDetail(orderId) // Refresh
            }
            is Resource.Error -> {
                snackbarHostState.showSnackbar(updateStatusState?.message ?: "Failed to update status")
                viewModel.resetUpdateState()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Order Detail",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Navy)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundWhite)
            )
        }
    ) { innerPadding ->
        when (val state = orderDetailState) {
            is Resource.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            }
            is Resource.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    ErrorState(
                        message = state.message ?: "Error loading order",
                        onRetry = { viewModel.fetchOrderDetail(orderId) }
                    )
                }
            }
            is Resource.Success -> {
                val order = state.data!!
                AdminOrderDetailContent(
                    order = order,
                    isUpdating = updateStatusState is Resource.Loading,
                    onUpdateStatus = { newStatus ->
                        viewModel.updateOrderStatus(orderId, newStatus)
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            }
        }
    }
}

@Composable
private fun AdminOrderDetailContent(
    order: OrderDetailDataDto,
    isUpdating: Boolean,
    onUpdateStatus: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header card
        item {
            AdminOrderHeaderCard(order)
        }

        // Status Update Section
        item {
            StatusUpdateCard(
                currentStatus = order.status ?: "pending",
                isUpdating = isUpdating,
                onUpdateStatus = onUpdateStatus
            )
        }

        // Shipping Info
        item {
            AdminShippingCard(order)
        }

        // Order Items Header
        item {
            Text(
                text = "Order Items (${order.items.size})",
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextPrimary
            )
        }

        // Order Items
        items(order.items) { item ->
            AdminOrderItemCard(item = item)
        }

        // Payment Summary
        item {
            AdminPaymentSummaryCard(order)
        }

        // Payment Info
        if (order.payment != null) {
            item {
                AdminPaymentInfoCard(order)
            }
        }

        // Note
        if (!order.note.isNullOrBlank()) {
            item {
                AdminNoteCard(order.note)
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

// ==================== Header Card ====================

@Composable
private fun AdminOrderHeaderCard(order: OrderDetailDataDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryBlueSoft),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = order.orderCode,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = PrimaryBlue
                )
                StatusBadge(status = order.status ?: "pending")
            }

            Spacer(modifier = Modifier.height(8.dp))

            order.createdAt?.let { date ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = date.take(19).replace("T", " "),
                        style = Typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Total: ${formatPrice(order.total)}đ",
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = PrimaryBlue
            )
        }
    }
}

// ==================== Status Update Card ====================

@Composable
private fun StatusUpdateCard(
    currentStatus: String,
    isUpdating: Boolean,
    onUpdateStatus: (String) -> Unit
) {
    val statusFlow = listOf("pending", "confirmed", "shipping", "delivered")
    val currentIndex = statusFlow.indexOf(currentStatus.lowercase())
    val isCancelled = currentStatus.lowercase() == "cancelled"

    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Update Status",
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (isCancelled || currentStatus.lowercase() == "delivered") {
                // Final state - no more updates
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isCancelled) Icons.Default.Cancel else Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (isCancelled) StatusError else StatusSuccess,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isCancelled) "This order has been cancelled"
                        else "This order has been delivered",
                        style = Typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            } else {
                // Current status display
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Current",
                            style = Typography.bodySmall,
                            color = TextSecondary
                        )
                        StatusBadge(status = currentStatus)
                    }

                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = TextHint,
                        modifier = Modifier.size(20.dp)
                    )

                    Column {
                        Text(
                            text = "Next",
                            style = Typography.bodySmall,
                            color = TextSecondary
                        )
                        if (currentIndex < statusFlow.lastIndex) {
                            StatusBadge(status = statusFlow[currentIndex + 1])
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Next status button
                    if (currentIndex < statusFlow.lastIndex) {
                        val nextStatus = statusFlow[currentIndex + 1]
                        Button(
                            onClick = { onUpdateStatus(nextStatus) },
                            enabled = !isUpdating,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryBlue,
                                contentColor = White,
                                disabledContainerColor = ButtonDisabled
                            )
                        ) {
                            if (isUpdating) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = "Mark as ${nextStatus.replaceFirstChar { it.uppercase() }}",
                                    fontFamily = Poppins,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp,
                                    color = White
                                )
                            }
                        }
                    }

                    // Cancel button
                    if (currentStatus.lowercase() != "cancelled") {
                        OutlinedButton(
                            onClick = { expanded = true },
                            enabled = !isUpdating,
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, StatusError),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = StatusError
                            )
                        ) {
                            Text(
                                text = "Cancel",
                                fontFamily = Poppins,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                // Cancel confirmation dropdown
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Confirm Cancel Order",
                                fontFamily = Poppins,
                                color = StatusError
                            )
                        },
                        onClick = {
                            expanded = false
                            onUpdateStatus("cancelled")
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = StatusError
                            )
                        }
                    )
                }
            }
        }
    }
}

// ==================== Shipping Card ====================

@Composable
private fun AdminShippingCard(order: OrderDetailDataDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Shipping Information",
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))

            AdminInfoRow(Icons.Default.Person, "Name", order.shippingName)
            AdminInfoRow(Icons.Default.Phone, "Phone", order.shippingPhone)
            AdminInfoRow(
                Icons.Default.LocationOn, "Address",
                buildString {
                    append(order.shippingAddress)
                    order.shippingWard?.let { append(", $it") }
                    order.shippingDistrict?.let { append(", $it") }
                    order.shippingCity?.let { append(", $it") }
                }
            )
        }
    }
}

@Composable
private fun AdminInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PrimaryBlue,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = label,
                style = Typography.bodySmall,
                color = TextHint
            )
            Text(
                text = value,
                style = Typography.bodyMedium,
                color = TextPrimary
            )
        }
    }
}

// ==================== Order Item Card ====================

@Composable
private fun AdminOrderItemCard(item: OrderItemDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (!item.productImage.isNullOrEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(item.productImage)
                        .crossfade(true)
                        .build(),
                    contentDescription = item.productName,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(BackgroundLight),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(BackgroundLight, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = null,
                        tint = TextHint,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.productName,
                    style = Typography.titleSmall,
                    color = TextPrimary,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${formatPrice(item.price)}đ × ${item.quantity}",
                        style = Typography.bodySmall,
                        color = TextSecondary
                    )
                    Text(
                        text = "${formatPrice(item.subtotal)}đ",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = PrimaryBlue
                    )
                }
            }
        }
    }
}

// ==================== Payment Summary Card ====================

@Composable
private fun AdminPaymentSummaryCard(order: OrderDetailDataDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Payment Summary",
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))

            PriceRow("Subtotal", formatPrice(order.subtotal) + "đ")
            PriceRow("Shipping Fee", formatPrice(order.shippingFee) + "đ")
            if (order.discount > 0) {
                PriceRow("Discount", "-${formatPrice(order.discount)}đ", StatusSuccess)
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = BorderLight)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextPrimary
                )
                Text(
                    text = "${formatPrice(order.total)}đ",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = PrimaryBlue
                )
            }
        }
    }
}

@Composable
private fun PriceRow(label: String, value: String, valueColor: Color = TextPrimary) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = Typography.bodyMedium, color = TextSecondary)
        Text(
            text = value,
            style = Typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = valueColor
        )
    }
}

// ==================== Payment Info Card ====================

@Composable
private fun AdminPaymentInfoCard(order: OrderDetailDataDto) {
    val payment = order.payment ?: return

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Payment Info",
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))

            PriceRow(
                "Method",
                when (payment.paymentMethod.lowercase()) {
                    "vnpay" -> "VNPay"
                    "zalopay" -> "ZaloPay"
                    else -> "COD"
                }
            )
            PriceRow("Amount", "${formatPrice(payment.amount)}đ")
            PriceRow(
                "Status",
                (payment.status ?: "pending").replaceFirstChar { it.uppercase() }
            )
            payment.transactionId?.let {
                PriceRow("Transaction", it)
            }
            payment.paidAt?.let {
                PriceRow("Paid At", it.take(19).replace("T", " "))
            }
        }
    }
}

// ==================== Note Card ====================

@Composable
private fun AdminNoteCard(note: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AccentGoldSoft),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Notes,
                contentDescription = null,
                tint = AccentGold,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Customer Note",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = TextPrimary
                )
                Text(
                    text = note,
                    style = Typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }
    }
}

// ==================== Status Badge ====================

@Composable
private fun StatusBadge(status: String) {
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
            fontFamily = Poppins,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp,
            color = textColor,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}
