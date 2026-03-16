package com.example.scamazon_frontend.ui.screens.admin.chat

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.scamazon_frontend.ui.components.LafyuuTopAppBar
import com.example.scamazon_frontend.ui.screens.chat.ChatBubble
import com.example.scamazon_frontend.ui.screens.chat.ChatInputBar
import com.example.scamazon_frontend.ui.theme.*

@Composable
fun AdminChatDetailScreen(
    chatRoomId: Int,
    viewModel: AdminChatDetailViewModel,
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

    LaunchedEffect(chatRoomId) { viewModel.loadMessages(chatRoomId) }

    val messages = (messagesState as? com.example.scamazon_frontend.core.utils.Resource.Success)?.data ?: emptyList()
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(0)
    }

    Scaffold(
        modifier = Modifier.imePadding(),
        contentWindowInsets = WindowInsets(0),
        topBar = {
            LafyuuTopAppBar(title = "Chat #$chatRoomId", onBackClick = onNavigateBack)
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
                isSending = isSending,
                placeholder = "Write a reply..."
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
                        Text("Chưa có tin nhắn", color = TextSecondary, fontFamily = Poppins)
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
                        // ADMIN VIEW: admin messages (isFromStore=true) → RIGHT
                        items(msgList.reversed(), key = { it.id }) { msg ->
                            ChatBubble(msg, isMine = msg.isFromStore == true)
                        }
                    }
                }
            }
        }
    }
}
