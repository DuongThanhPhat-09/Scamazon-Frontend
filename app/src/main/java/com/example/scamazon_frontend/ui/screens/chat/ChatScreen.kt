package com.example.scamazon_frontend.ui.screens.chat

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.scamazon_frontend.data.models.chat.ChatMessageDto
import com.example.scamazon_frontend.ui.components.LafyuuTopAppBar
import com.example.scamazon_frontend.ui.theme.*

@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    onNavigateBack: () -> Unit = {}
) {
    val messagesState by viewModel.messagesState.collectAsStateWithLifecycle()
    val isSending by viewModel.isSending.collectAsStateWithLifecycle()
    var messageText by remember { mutableStateOf("") }
    val context = LocalContext.current
    val listState = rememberLazyListState()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) viewModel.sendImageMessage(context, uri)
    }

    LaunchedEffect(Unit) { viewModel.startOrLoadChat() }

    val messages = (messagesState as? com.example.scamazon_frontend.core.utils.Resource.Success)?.data ?: emptyList()
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(0)
    }

    // imePadding on Scaffold so both messages + input bar move together when keyboard opens
    Scaffold(
        modifier = Modifier.imePadding(),
        contentWindowInsets = WindowInsets(0),
        topBar = {
            LafyuuTopAppBar(title = "Scamazon Support", onBackClick = onNavigateBack)
        },
        bottomBar = {
            ChatInputBar(
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
                Box(Modifier.fillMaxSize().padding(paddingValues), Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            }
            is com.example.scamazon_frontend.core.utils.Resource.Error -> {
                Box(Modifier.fillMaxSize().padding(paddingValues), Alignment.Center) {
                    Text(state.message ?: "Error", color = StatusError)
                }
            }
            is com.example.scamazon_frontend.core.utils.Resource.Success -> {
                val msgList = state.data ?: emptyList()
                if (msgList.isEmpty()) {
                    Box(Modifier.fillMaxSize().padding(paddingValues), Alignment.Center) {
                        Text("Hãy gửi tin nhắn đầu tiên!", color = TextSecondary, fontFamily = Poppins)
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        reverseLayout = true,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .background(BackgroundWhite),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(msgList.reversed(), key = { it.id }) { msg ->
                            ChatBubble(msg, isMine = msg.isFromStore != true)
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun ChatBubble(msg: ChatMessageDto, isMine: Boolean) {
    val alignment = if (isMine) Alignment.End else Alignment.Start
    val bgColor = if (isMine) PrimaryBlue else BackgroundLight
    val textColor = if (isMine) White else TextPrimary
    val shape = if (isMine) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp)
    } else {
        RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    }

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = alignment) {
        val displayName = msg.senderName?.takeIf { it.isNotBlank() && it.lowercase() != "string" }
        if (!isMine && displayName != null) {
            Text(
                text = displayName,
                color = PrimaryBlue,
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp
            )
            Spacer(Modifier.height(2.dp))
        }

        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(shape)
                .background(bgColor)
                .padding(horizontal = 14.dp, vertical = 10.dp)
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
                    if (msg.content.isNotBlank() && msg.content != "📷 Ảnh") Spacer(Modifier.height(6.dp))
                }
                if (msg.content.isNotBlank() && msg.content != "📷 Ảnh") {
                    Text(msg.content, color = textColor, fontFamily = Poppins, fontSize = 14.sp, lineHeight = 20.sp)
                }
            }
        }

        Spacer(Modifier.height(2.dp))
        Text(formatChatTime(msg.createdAt), color = TextHint, fontFamily = Poppins, fontSize = 10.sp)
    }
}

internal fun formatChatTime(dateStr: String?): String {
    if (dateStr == null) return ""
    return try {
        val parts = dateStr.split("T")
        if (parts.size >= 2) parts[1].substring(0, 5) else dateStr
    } catch (e: Exception) { dateStr }
}

@Composable
internal fun ChatInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    onImageClick: () -> Unit,
    isSending: Boolean = false,
    placeholder: String = "Message..."
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = White,
        shadowElevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // + button (attach image)
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(BackgroundLight),
                contentAlignment = Alignment.Center
            ) {
                if (isSending) {
                    CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp, color = PrimaryBlue)
                } else {
                    IconButton(onClick = onImageClick, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Add, contentDescription = "Attach", tint = TextSecondary, modifier = Modifier.size(20.dp))
                    }
                }
            }

            // Message input pill
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(BackgroundLight)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (text.isEmpty()) {
                    Text(placeholder, color = TextHint, fontFamily = Poppins, fontSize = 14.sp)
                }
                BasicTextField(
                    value = text,
                    onValueChange = onTextChange,
                    textStyle = TextStyle(fontFamily = Poppins, fontSize = 14.sp, color = TextPrimary),
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Send button
            IconButton(
                onClick = onSend,
                enabled = text.isNotBlank() && !isSending,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = if (text.isNotBlank() && !isSending) PrimaryBlue else TextHint,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
