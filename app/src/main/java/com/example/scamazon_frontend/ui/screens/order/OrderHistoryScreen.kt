package com.example.scamazon_frontend.ui.screens.order

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scamazon_frontend.ui.components.EmptyState
import com.example.scamazon_frontend.ui.components.LafyuuPrimaryButton
import com.example.scamazon_frontend.ui.components.LafyuuTopAppBar
import com.example.scamazon_frontend.ui.theme.*

@Composable
fun OrderHistoryScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToOrderDetail: (String) -> Unit = {}
) {
    val tabs = listOf("All", "Active", "Completed", "Cancelled")
    var selectedTab by remember { mutableStateOf(0) }

    val allOrders = remember {
        listOf(
            Order("ORD-001", "2 items", 299.43, OrderStatus.DELIVERED, "Jan 15, 2026"),
            Order("ORD-002", "1 item", 199.99, OrderStatus.PROCESSING, "Jan 20, 2026"),
            Order("ORD-003", "3 items", 589.97, OrderStatus.SHIPPED, "Jan 22, 2026"),
            Order("ORD-004", "1 item", 249.99, OrderStatus.CANCELLED, "Jan 10, 2026"),
            Order("ORD-005", "2 items", 459.98, OrderStatus.DELIVERED, "Dec 28, 2025"),
            Order("ORD-006", "1 item", 89.99, OrderStatus.DELIVERED, "Dec 15, 2025")
        )
    }

    val filteredOrders = when (selectedTab) {
        1 -> allOrders.filter { it.status == OrderStatus.PROCESSING || it.status == OrderStatus.SHIPPED }
        2 -> allOrders.filter { it.status == OrderStatus.DELIVERED }
        3 -> allOrders.filter { it.status == OrderStatus.CANCELLED }
        else -> allOrders
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
    ) {
        LafyuuTopAppBar(
            title = "My Orders",
            onBackClick = onNavigateBack
        )

        // Tab Row
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = White,
            contentColor = PrimaryBlue,
            edgePadding = 16.dp,
            divider = {},
            indicator = {
                TabRowDefaults.SecondaryIndicator(
                    color = PrimaryBlue
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontFamily = Poppins,
                            fontWeight = if (selectedTab == index) FontWeight.SemiBold else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    },
                    selectedContentColor = PrimaryBlue,
                    unselectedContentColor = TextSecondary
                )
            }
        }

        HorizontalDivider(color = BorderLight)

        if (filteredOrders.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                EmptyState(
                    title = "No Orders Yet",
                    message = "You haven't placed any orders in this category"
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
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredOrders, key = { it.id }) { order ->
                    OrderCard(
                        order = order,
                        onClick = { onNavigateToOrderDetail(order.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderCard(
    order: Order,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = LafyuuShapes.CardShape,
        colors = CardDefaults.cardColors(containerColor = White),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Order Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(PrimaryBlueSoft, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = order.id,
                            fontFamily = Poppins,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = order.date,
                            fontFamily = Poppins,
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
                OrderStatusBadge(status = order.status)
            }

            HorizontalDivider(color = BorderLight)

            // Order Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = order.itemCount,
                        fontFamily = Poppins,
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "Total: $${"%.2f".format(order.total)}",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = PrimaryBlue
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "Details",
                        fontFamily = Poppins,
                        fontSize = 13.sp,
                        color = PrimaryBlue,
                        fontWeight = FontWeight.Medium
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Action buttons for active orders
            if (order.status == OrderStatus.SHIPPED) {
                OutlinedButton(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    shape = LafyuuShapes.ButtonShape,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryBlue)
                ) {
                    Text(
                        text = "Track Package",
                        fontFamily = Poppins,
                        fontSize = 13.sp
                    )
                }
            } else if (order.status == OrderStatus.DELIVERED) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {},
                        modifier = Modifier.weight(1f),
                        shape = LafyuuShapes.ButtonShape,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryBlue)
                    ) {
                        Text(
                            text = "Review",
                            fontFamily = Poppins,
                            fontSize = 13.sp
                        )
                    }
                    Button(
                        onClick = {},
                        modifier = Modifier.weight(1f),
                        shape = LafyuuShapes.ButtonShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBlue,
                            contentColor = White
                        )
                    ) {
                        Text(
                            text = "Reorder",
                            fontFamily = Poppins,
                            fontSize = 13.sp,
                            color = White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderStatusBadge(status: OrderStatus) {
    val (text, color, bgColor) = when (status) {
        OrderStatus.PROCESSING -> Triple("Processing", AccentGold, AccentGoldSoft)
        OrderStatus.SHIPPED -> Triple("Shipped", PrimaryBlue, PrimaryBlueSoft)
        OrderStatus.DELIVERED -> Triple("Delivered", StatusSuccess, Color(0xFFE8FAF6))
        OrderStatus.CANCELLED -> Triple("Cancelled", StatusError, Color(0xFFFFEEF0))
    }

    Surface(
        shape = LafyuuShapes.ChipShape,
        color = bgColor
    ) {
        Text(
            text = text,
            fontFamily = Poppins,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            color = color,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

private enum class OrderStatus {
    PROCESSING, SHIPPED, DELIVERED, CANCELLED
}

private data class Order(
    val id: String,
    val itemCount: String,
    val total: Double,
    val status: OrderStatus,
    val date: String
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OrderHistoryScreenPreview() {
    ScamazonFrontendTheme {
        OrderHistoryScreen()
    }
}
