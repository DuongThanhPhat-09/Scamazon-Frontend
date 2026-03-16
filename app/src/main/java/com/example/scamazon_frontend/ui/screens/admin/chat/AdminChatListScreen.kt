package com.example.scamazon_frontend.ui.screens.admin.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.scamazon_frontend.data.models.chat.ChatRoomSummaryDto
import com.example.scamazon_frontend.ui.components.LafyuuTopAppBar
import com.example.scamazon_frontend.ui.theme.*

@Composable
fun AdminChatListScreen(
    viewModel: AdminChatListViewModel,
    onNavigateToChatDetail: (Int) -> Unit,
    onNavigateBack: () -> Unit = {}
) {
    val conversationsState by viewModel.conversationsState.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            LafyuuTopAppBar(title = "Messages", onBackClick = onNavigateBack)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BackgroundWhite)
        ) {
            // Search bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(BackgroundLight)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = TextHint, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Box(Modifier.weight(1f)) {
                        if (searchQuery.isEmpty()) {
                            Text("Search Message...", color = TextHint, fontFamily = Poppins, fontSize = 14.sp)
                        }
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            textStyle = TextStyle(fontFamily = Poppins, fontSize = 14.sp, color = TextPrimary),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            HorizontalDivider(color = BorderLight)

            when (val state = conversationsState) {
                is com.example.scamazon_frontend.core.utils.Resource.Loading -> {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        CircularProgressIndicator(color = PrimaryBlue)
                    }
                }
                is com.example.scamazon_frontend.core.utils.Resource.Error -> {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        Text(state.message ?: "Error", color = StatusError)
                    }
                }
                is com.example.scamazon_frontend.core.utils.Resource.Success -> {
                    val allChats = state.data ?: emptyList()
                    val chats = if (searchQuery.isBlank()) allChats
                    else allChats.filter {
                        (it.userName ?: "").contains(searchQuery, ignoreCase = true) ||
                                (it.lastMessage ?: "").contains(searchQuery, ignoreCase = true)
                    }

                    if (chats.isEmpty()) {
                        Box(Modifier.fillMaxSize(), Alignment.Center) {
                            Text("No conversations yet", color = TextSecondary, fontFamily = Poppins)
                        }
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(chats, key = { it.id }) { chat ->
                                ChatRoomItem(chat, onClick = { onNavigateToChatDetail(chat.id) })
                                HorizontalDivider(
                                    modifier = Modifier.padding(start = 80.dp),
                                    color = BorderLight,
                                    thickness = 0.5.dp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatRoomItem(chat: ChatRoomSummaryDto, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar with online indicator dot
        Box(modifier = Modifier.size(52.dp)) {
            if (!chat.userAvatar.isNullOrBlank()) {
                AsyncImage(
                    model = chat.userAvatar,
                    contentDescription = chat.userName,
                    modifier = Modifier.size(52.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier.size(52.dp).clip(CircleShape).background(BackgroundLight),
                    contentAlignment = Alignment.Center
                ) {
                    val initial = chat.userName?.firstOrNull()?.uppercaseChar()
                    if (initial != null) {
                        Text(
                            text = initial.toString(),
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = PrimaryBlue
                        )
                    } else {
                        Icon(Icons.Default.Person, contentDescription = null, tint = TextHint, modifier = Modifier.size(24.dp))
                    }
                }
            }
            // Online dot (show if status is "open" / active)
            if (chat.status == "open") {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .align(Alignment.BottomEnd)
                        .clip(CircleShape)
                        .background(White)
                        .padding(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(StatusSuccess)
                    )
                }
            }
        }

        Spacer(Modifier.width(14.dp))

        // Text content
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = chat.userName ?: "Unknown",
                    fontFamily = Poppins,
                    fontWeight = if (chat.unreadCount > 0) FontWeight.Bold else FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = formatChatListTime(chat.lastMessageAt ?: chat.createdAt),
                    fontFamily = Poppins,
                    fontSize = 11.sp,
                    color = if (chat.unreadCount > 0) PrimaryBlue else TextHint
                )
            }

            Spacer(Modifier.height(3.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = chat.lastMessage ?: "No messages yet",
                    fontFamily = Poppins,
                    fontSize = 13.sp,
                    color = if (chat.unreadCount > 0) TextPrimary else TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                if (chat.unreadCount > 0) {
                    Spacer(Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(PrimaryBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (chat.unreadCount > 99) "99+" else chat.unreadCount.toString(),
                            color = White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Poppins
                        )
                    }
                }
            }
        }
    }
}

private fun formatChatListTime(dateStr: String?): String {
    if (dateStr == null) return ""
    return try {
        val parts = dateStr.split("T")
        if (parts.size >= 2) parts[1].substring(0, 5) else dateStr
    } catch (e: Exception) { dateStr }
}
