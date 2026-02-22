package com.example.scamazon_frontend.ui.screens.admin.chat

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.scamazon_frontend.data.models.chat.ChatMessageDto
import com.example.scamazon_frontend.ui.components.LafyuuTopAppBar
import com.example.scamazon_frontend.ui.theme.*

@Composable
fun AdminChatDetailScreen(
    chatRoomId: Int,
    viewModel: AdminChatDetailViewModel,
    onNavigateBack: () -> Unit = {}
) {
    val messagesState by viewModel.messagesState.collectAsState()
    val isSending by viewModel.isSending.collectAsState()
    var messageText by remember { mutableStateOf("") }
    val context = LocalContext.current
    val listState = rememberLazyListState()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.sendImageMessage(context, uri)
        }
    }
    
    LaunchedEffect(chatRoomId) {
        viewModel.loadMessages(chatRoomId)
    }

    val messages = (messagesState as? com.example.scamazon_frontend.core.utils.Resource.Success)?.data ?: emptyList()
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            LafyuuTopAppBar(
                title = "Chat #$chatRoomId",
                onBackClick = onNavigateBack
            )
        },
        bottomBar = {
            AdminChatInputBar(
                text = messageText,
                onTextChange = { messageText = it },
                onSend = {
                    if (messageText.isNotBlank()) {
                        viewModel.sendMessage(messageText)
                        messageText = ""
                    }
                },
                onImageClick = { imagePickerLauncher.launch("image/*") },
                isSending = isSending
            )
        }
    ) { paddingValues ->
        when (val state = messagesState) {
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
                val msgList = state.data ?: emptyList()
                if (msgList.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                        Text("Chưa có tin nhắn", color = TextSecondary, fontFamily = Poppins)
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .background(BackgroundWhite),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(msgList) { msg ->
                            // ADMIN VIEW: admin messages (isFromStore=true) → RIGHT
                            //             customer messages (isFromStore=false) → LEFT
                            AdminChatBubble(msg, isMine = msg.isFromStore == true)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminChatBubble(msg: ChatMessageDto, isMine: Boolean) {
    val alignment = if (isMine) Alignment.End else Alignment.Start
    val bgColor = if (isMine) PrimaryBlue else BackgroundLight
    val textColor = if (isMine) White else TextPrimary
    val shape = if (isMine) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp)
    } else {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 16.dp)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        if (!isMine && msg.senderName != null) {
            Text(
                text = msg.senderName,
                color = PrimaryBlue,
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
        }

        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(shape)
                .background(bgColor)
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Column {
                if (!msg.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = msg.imageUrl,
                        contentDescription = "Chat image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
                if (msg.content.isNotBlank() && msg.content != "📷 Ảnh") {
                    Text(
                        text = msg.content,
                        color = textColor,
                        fontFamily = Poppins,
                        fontSize = 14.sp
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = formatTime(msg.createdAt),
            color = TextHint,
            fontFamily = Poppins,
            fontSize = 10.sp
        )
    }
}

private fun formatTime(dateStr: String?): String {
    if (dateStr == null) return ""
    return try {
        val parts = dateStr.split("T")
        if (parts.size >= 2) parts[1].substring(0, 5) else dateStr
    } catch (e: Exception) { dateStr }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdminChatInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    onImageClick: () -> Unit,
    isSending: Boolean = false
) {
    Surface(
        modifier = Modifier.fillMaxWidth().imePadding(),
        color = White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onImageClick, enabled = !isSending) {
                if (isSending) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp, color = PrimaryBlue)
                } else {
                    Icon(Icons.Default.Image, contentDescription = "Attach Image", tint = TextSecondary)
                }
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Write a reply...", color = TextHint, fontFamily = Poppins) },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = BorderLight,
                    focusedContainerColor = BackgroundLight,
                    unfocusedContainerColor = BackgroundLight
                ),
                maxLines = 4
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            IconButton(onClick = onSend, enabled = text.isNotBlank() && !isSending) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = if (text.isNotBlank() && !isSending) PrimaryBlue else TextHint
                )
            }
        }
    }
}
