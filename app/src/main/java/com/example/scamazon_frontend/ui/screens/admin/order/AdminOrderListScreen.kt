package com.example.scamazon_frontend.ui.screens.admin.order

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.scamazon_frontend.core.utils.Resource
import com.example.scamazon_frontend.core.utils.formatPrice
import com.example.scamazon_frontend.data.models.order.AdminOrderSummaryDto
import com.example.scamazon_frontend.di.ViewModelFactory
import com.example.scamazon_frontend.ui.components.*
import com.example.scamazon_frontend.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrderListScreen(
    viewModel: AdminOrderViewModel = viewModel(factory = ViewModelFactory(LocalContext.current)),
    onNavigateBack: () -> Unit = {},
    onOrderClick: (Int) -> Unit = {}
) {
    val ordersState by viewModel.ordersState.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Manage Orders",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Navy
                        )
                    }
                },
                actions = {},
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundWhite
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BackgroundLight)
        ) {
            // Status filter chips
            StatusFilterRow(
                selectedFilter = selectedFilter,
                onFilterChange = { viewModel.onFilterChange(it) }
            )

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
                        ErrorState(
                            message = ordersState.message ?: "Error loading orders",
                            onRetry = { viewModel.fetchOrders() }
                        )
                    }
                }
                is Resource.Success -> {
                    val allOrders = ordersState.data ?: emptyList()
                    val filteredOrders = if (selectedFilter == "all") {
                        allOrders
                    } else {
                        allOrders.filter { it.status?.lowercase() == selectedFilter }
                    }

                    if (filteredOrders.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            EmptyState(
                                title = "No Orders Found",
                                message = if (selectedFilter == "all") "There are no orders yet"
                                else "No orders with status: ${selectedFilter.replaceFirstChar { it.uppercase() }}"
                            )
                        }
                    } else {
                        // Orders count
                        Text(
                            text = "${filteredOrders.size} order(s)",
                            style = Typography.bodySmall,
                            color = TextSecondary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )

                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredOrders, key = { it.id }) { order ->
                                AdminOrderCard(
                                    order = order,
                                    onClick = { onOrderClick(order.id) }
                                )
                            }

                            item { Spacer(modifier = Modifier.height(16.dp)) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusFilterRow(
    selectedFilter: String,
    onFilterChange: (String) -> Unit
) {
    val filters = listOf(
        "all" to "All",
        "pending" to "Pending",
        "confirmed" to "Confirmed",
        "shipping" to "Shipping",
        "delivered" to "Delivered",
        "cancelled" to "Cancelled"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundWhite)
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filters.forEach { (value, label) ->
            val isSelected = selectedFilter == value

            FilterChip(
                selected = isSelected,
                onClick = { onFilterChange(value) },
                label = {
                    Text(
                        text = label,
                        fontFamily = Poppins,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        fontSize = 12.sp
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = PrimaryBlue,
                    selectedLabelColor = White,
                    containerColor = White,
                    labelColor = TextSecondary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = BorderDefault,
                    selectedBorderColor = PrimaryBlue,
                    enabled = true,
                    selected = isSelected
                )
            )
        }
    }
}

@Composable
private fun AdminOrderCard(
    order: AdminOrderSummaryDto,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Order Code + Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = order.orderCode,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = PrimaryBlue
                )

                AdminOrderStatusBadge(status = order.status ?: "pending")
            }

            // Customer name
            order.customerName?.let { name ->
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = TextHint,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = name,
                        style = Typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(color = BorderLight)

            Spacer(modifier = Modifier.height(12.dp))

            // Info row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "${order.itemCount} item${if (order.itemCount > 1) "s" else ""}",
                        style = Typography.bodySmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${formatPrice(order.total)}đ",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextPrimary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    order.createdAt?.let { date ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = TextHint,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = date.take(10),
                                style = Typography.bodySmall,
                                color = TextHint
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Navigate icon
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "View Detail",
                        tint = TextHint,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AdminOrderStatusBadge(status: String) {
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
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}
