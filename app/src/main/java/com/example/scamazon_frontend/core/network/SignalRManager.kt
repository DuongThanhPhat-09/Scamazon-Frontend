package com.example.scamazon_frontend.core.network

import android.content.Context
import android.util.Log
import com.example.scamazon_frontend.core.utils.TokenManager
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

import com.example.scamazon_frontend.data.models.chat.ChatMessageDto
import com.example.scamazon_frontend.data.models.notification.NotificationDto

enum class AppEvent {
    OrderUpdated,
    ProductUpdated,
    NotificationReceived
}

class SignalRManager private constructor(context: Context) {
    private val tokenManager = TokenManager(context)
    private var hubConnection: HubConnection? = null
    private var chatHubConnection: HubConnection? = null

    // SharedFlow to emit events to whoever is listening (ViewModels)
    private val _events = MutableSharedFlow<AppEvent>(extraBufferCapacity = 10)
    val events: SharedFlow<AppEvent> = _events.asSharedFlow()

    private val _chatEvents = MutableSharedFlow<ChatMessageDto>(extraBufferCapacity = 10)
    val chatEvents: SharedFlow<ChatMessageDto> = _chatEvents.asSharedFlow()

    init {
        // Use the same BASE_URL, but point to the hub
        val baseUrl = "http://10.0.2.2:5255"

        try {
            hubConnection = HubConnectionBuilder.create("$baseUrl/app-hub")
                .withAccessTokenProvider(io.reactivex.rxjava3.core.Single.defer {
                    io.reactivex.rxjava3.core.Single.just(tokenManager.getToken() ?: "")
                })
                .build()

            // Setup listeners *before* starting
            hubConnection?.on("OrderUpdated") {
                Log.d("SignalRManager", "Received OrderUpdated event")
                _events.tryEmit(AppEvent.OrderUpdated)
            }

            hubConnection?.on("ProductUpdated") {
                Log.d("SignalRManager", "Received ProductUpdated event")
                _events.tryEmit(AppEvent.ProductUpdated)
            }

            hubConnection?.on("ReceiveNotification", { 
                Log.d("SignalRManager", "Received NotificationReceived event")
                _events.tryEmit(AppEvent.NotificationReceived)
            })

            hubConnection?.onClosed { error ->
                Log.e("SignalRManager", "AppHub Connection closed. Error: ${error?.message}")
            }

            chatHubConnection = HubConnectionBuilder.create("$baseUrl/chathub")
                .withAccessTokenProvider(io.reactivex.rxjava3.core.Single.defer {
                    io.reactivex.rxjava3.core.Single.just(tokenManager.getToken() ?: "")
                })
                .build()

            chatHubConnection?.on("ReceiveMessage", { message: ChatMessageDto ->
                Log.d("SignalRManager", "New message: \${message.content}")
                _chatEvents.tryEmit(message)
            }, ChatMessageDto::class.java)

            chatHubConnection?.onClosed { error ->
                Log.e("SignalRManager", "ChatHub Connection closed: ${error?.message}")
            }
            
            // Start connection immediately
            startConnection()
        } catch (e: Exception) {
            Log.e("SignalRManager", "Error building SignalR connection: ${e.message}")
        }
    }

    fun startConnection() {
        if (hubConnection?.connectionState == com.microsoft.signalr.HubConnectionState.DISCONNECTED) {
            try {
                hubConnection?.start()?.doOnError { e ->
                    Log.e("SignalRManager", "Error starting app hub: ${e.message}")
                }?.onErrorComplete()?.subscribe() 
            } catch (e: Exception) {
                Log.e("SignalRManager", "Connection Exception: ${e.message}")
            }
        }
        if (chatHubConnection?.connectionState == com.microsoft.signalr.HubConnectionState.DISCONNECTED) {
            try {
                chatHubConnection?.start()?.doOnError { e ->
                    Log.e("SignalRManager", "Error starting chat hub: ${e.message}")
                }?.onErrorComplete()?.subscribe() 
            } catch (e: Exception) {}
        }
    }

    fun joinChatRoom(chatRoomId: Int) {
        try {
            if (chatHubConnection?.connectionState == com.microsoft.signalr.HubConnectionState.CONNECTED) {
                chatHubConnection?.invoke("JoinChatRoom", chatRoomId)
                Log.d("SignalRManager", "Joined chat room: $chatRoomId")
            } else {
                Log.w("SignalRManager", "Cannot join room $chatRoomId - ChatHub not connected")
            }
        } catch (e: Exception) {
            Log.e("SignalRManager", "Error joining chat room: ${e.message}")
        }
    }

    fun leaveChatRoom(chatRoomId: Int) {
        try {
            if (chatHubConnection?.connectionState == com.microsoft.signalr.HubConnectionState.CONNECTED) {
                chatHubConnection?.invoke("LeaveChatRoom", chatRoomId)
                Log.d("SignalRManager", "Left chat room: $chatRoomId")
            }
        } catch (e: Exception) {
            Log.e("SignalRManager", "Error leaving chat room: ${e.message}")
        }
    }

    fun stopConnection() {
        if (hubConnection?.connectionState == com.microsoft.signalr.HubConnectionState.CONNECTED) {
            hubConnection?.stop()
        }
        if (chatHubConnection?.connectionState == com.microsoft.signalr.HubConnectionState.CONNECTED) {
            chatHubConnection?.stop()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: SignalRManager? = null

        fun getInstance(context: Context): SignalRManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SignalRManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
