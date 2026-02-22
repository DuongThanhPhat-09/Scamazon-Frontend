package com.example.scamazon_frontend.ui.screens.admin.chat

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scamazon_frontend.core.network.SignalRManager
import com.example.scamazon_frontend.core.utils.Resource
import com.example.scamazon_frontend.data.models.chat.ChatMessageDto
import com.example.scamazon_frontend.data.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminChatDetailViewModel(
    private val repository: ChatRepository,
    private val signalRManager: SignalRManager
) : ViewModel() {

    private val _messagesState = MutableStateFlow<Resource<List<ChatMessageDto>>>(Resource.Loading())
    val messagesState = _messagesState.asStateFlow()

    private val _isSending = MutableStateFlow(false)
    val isSending = _isSending.asStateFlow()

    private var currentRoomId: Int? = null

    init {
        viewModelScope.launch {
            signalRManager.chatEvents.collect { newMessage ->
                if (newMessage.chatRoomId == currentRoomId) {
                    addMessageToList(newMessage)
                }
            }
        }
    }

    fun loadMessages(roomId: Int) {
        currentRoomId?.let { signalRManager.leaveChatRoom(it) }
        currentRoomId = roomId
        signalRManager.joinChatRoom(roomId)

        viewModelScope.launch {
            _messagesState.value = Resource.Loading()
            when (val msgRes = repository.getMessages(roomId)) {
                is Resource.Success -> {
                    val msgs = msgRes.data?.data?.messages?.sortedBy { it.createdAt } ?: emptyList()
                    _messagesState.value = Resource.Success(msgs)
                }
                is Resource.Error -> {
                    _messagesState.value = Resource.Error(msgRes.message ?: "Failed to load messages")
                }
                else -> Unit
            }
        }
    }

    fun sendMessage(content: String) {
        val roomId = currentRoomId ?: return
        viewModelScope.launch {
            val result = repository.sendMessage(roomId, content)
            if (result is Resource.Success && result.data?.data != null) {
                addMessageToList(result.data.data)
            }
        }
    }

    fun sendImageMessage(context: Context, uri: Uri) {
        val roomId = currentRoomId ?: return
        viewModelScope.launch {
            _isSending.value = true
            when (val uploadResult = repository.uploadImage(context, uri)) {
                is Resource.Success -> {
                    val imageUrl = uploadResult.data ?: ""
                    val result = repository.sendMessage(roomId, "📷 Ảnh", imageUrl)
                    if (result is Resource.Success && result.data?.data != null) {
                        addMessageToList(result.data.data)
                    }
                }
                is Resource.Error -> {}
                else -> Unit
            }
            _isSending.value = false
        }
    }

    private fun addMessageToList(message: ChatMessageDto) {
        val currentList = _messagesState.value.data?.toMutableList() ?: mutableListOf()
        if (currentList.none { it.id == message.id }) {
            currentList.add(message)
            _messagesState.value = Resource.Success(currentList.sortedBy { it.createdAt })
        }
    }

    override fun onCleared() {
        super.onCleared()
        currentRoomId?.let { signalRManager.leaveChatRoom(it) }
    }
}
