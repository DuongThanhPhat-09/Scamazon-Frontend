package com.example.scamazon_frontend.ui.screens.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scamazon_frontend.ui.components.LafyuuTopAppBar
import com.example.scamazon_frontend.ui.theme.*

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.scamazon_frontend.data.models.notification.NotificationDto

@Composable
fun NotificationScreen(
    viewModel: NotificationViewModel,
    onNavigateBack: () -> Unit = {}
) {
    val state by viewModel.notificationsState.collectAsState()

    Scaffold(
        topBar = {
            LafyuuTopAppBar(
                title = "Notifications",
                onBackClick = onNavigateBack
            )
        }
    ) { paddingValues ->
        when (state) {
            is com.example.scamazon_frontend.core.utils.Resource.Loading -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            }
            is com.example.scamazon_frontend.core.utils.Resource.Error -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Text(text = state.message ?: "Error", color = StatusError)
                }
            }
            is com.example.scamazon_frontend.core.utils.Resource.Success -> {
                val notifications = state.data ?: emptyList()
                if (notifications.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No notifications yet", color = TextSecondary, fontFamily = Poppins)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .background(BackgroundWhite),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(notifications) { notif ->
                            NotificationItem(notif)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationItem(notif: NotificationDto) {
    val unread = notif.isRead == false
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (unread) PrimaryBlueSoft else White
        ),
        shape = LafyuuShapes.CardShape,
        elevation = CardDefaults.cardElevation(defaultElevation = if (unread) 2.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Icon Background
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(PrimaryBlue.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                val icon = when(notif.type) {
                    "order_status" -> Icons.Default.ShoppingBag
                    "chat" -> Icons.Default.Notifications
                    else -> Icons.Default.Notifications
                }
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notif.title,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = notif.body ?: "",
                    fontFamily = Poppins,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = notif.createdAt ?: "",
                    fontFamily = Poppins,
                    fontSize = 10.sp,
                    color = TextHint
                )
            }
            
            if (unread) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(StatusError)
                        .align(Alignment.CenterVertically)
                )
            }
        }
    }
}

data class NotificationMock(
    val id: Int,
    val title: String,
    val body: String,
    val type: String,
    val date: String,
    val unread: Boolean
)
