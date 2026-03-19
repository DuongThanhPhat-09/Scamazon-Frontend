package com.example.scamazon_frontend.core.network

import android.content.Context
import android.util.Log
import com.example.scamazon_frontend.core.utils.TokenManager
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

import com.example.scamazon_frontend.data.models.chat.ChatMessageDto
import com.example.scamazon_frontend.data.models.notification.NotificationDto
import java.util.concurrent.CopyOnWriteArraySet

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

    // Track joined rooms so we can re-join on reconnect
    private val joinedRooms = CopyOnWriteArraySet<Int>()

    // Track pending room joins (requested before connection was ready)
    private val pendingRoomJoins = CopyOnWriteArraySet<Int>()

    init {
        // Deployed backend on Render
        val baseUrl = "https://scamazon-backend-nman.onrender.com"

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
                scheduleReconnect()
            }

            chatHubConnection = HubConnectionBuilder.create("$baseUrl/chathub")
                .withAccessTokenProvider(io.reactivex.rxjava3.core.Single.defer {
                    io.reactivex.rxjava3.core.Single.just(tokenManager.getToken() ?: "")
                })
                .build()

            chatHubConnection?.on("ReceiveMessage", { message: ChatMessageDto ->
                Log.d("SignalRManager", "New message in room ${message.chatRoomId}: ${message.content}")
                _chatEvents.tryEmit(message)
            }, ChatMessageDto::class.java)

            chatHubConnection?.onClosed { error ->
                Log.e("SignalRManager", "ChatHub Connection closed: ${error?.message}")
                scheduleReconnect()
            }

            // Start connection immediately
            startConnection()
        } catch (e: Exception) {
            Log.e("SignalRManager", "Error building SignalR connection: ${e.message}")
        }
    }

    private fun scheduleReconnect() {
        Thread {
            try {
                Thread.sleep(3000)
                Log.d("SignalRManager", "Attempting reconnect...")
                startConnection()
            } catch (e: Exception) {
                Log.e("SignalRManager", "Reconnect failed: ${e.message}")
            }
        }.start()
    }

    fun startConnection() {
        if (hubConnection?.connectionState == HubConnectionState.DISCONNECTED) {
            try {
                hubConnection?.start()?.doOnError { e ->
                    Log.e("SignalRManager", "Error starting app hub: ${e.message}")
                }?.onErrorComplete()?.subscribe()
            } catch (e: Exception) {
                Log.e("SignalRManager", "Connection Exception: ${e.message}")
            }
        }
        if (chatHubConnection?.connectionState == HubConnectionState.DISCONNECTED) {
            try {
                chatHubConnection?.start()?.doOnComplete {
                    Log.d("SignalRManager", "ChatHub connected successfully")
                    // Re-join all tracked rooms after reconnect
                    val roomsToJoin = HashSet<Int>().apply {
                        addAll(joinedRooms)
                        addAll(pendingRoomJoins)
                    }
                    pendingRoomJoins.clear()
                    for (roomId in roomsToJoin) {
                        try {
                            chatHubConnection?.invoke("JoinChatRoom", roomId)
                            joinedRooms.add(roomId)
                            Log.d("SignalRManager", "Re-joined chat room: $roomId")
                        } catch (e: Exception) {
                            Log.e("SignalRManager", "Error re-joining room $roomId: ${e.message}")
                        }
                    }
                }?.doOnError { e ->
                    Log.e("SignalRManager", "Error starting chat hub: ${e.message}")
                }?.onErrorComplete()?.subscribe()
            } catch (e: Exception) {
                Log.e("SignalRManager", "ChatHub start exception: ${e.message}")
            }
        }
    }

    fun joinChatRoom(chatRoomId: Int) {
        joinedRooms.add(chatRoomId)
        try {
            if (chatHubConnection?.connectionState == HubConnectionState.CONNECTED) {
                chatHubConnection?.invoke("JoinChatRoom", chatRoomId)
                Log.d("SignalRManager", "Joined chat room: $chatRoomId")
            } else {
                // Connection not ready yet — queue for when it connects
                pendingRoomJoins.add(chatRoomId)
                Log.w("SignalRManager", "ChatHub not connected, queued room $chatRoomId for join on connect")
                // Try to start connection if disconnected
                startConnection()
            }
        } catch (e: Exception) {
            Log.e("SignalRManager", "Error joining chat room: ${e.message}")
        }
    }

    fun leaveChatRoom(chatRoomId: Int) {
        joinedRooms.remove(chatRoomId)
        pendingRoomJoins.remove(chatRoomId)
        try {
            if (chatHubConnection?.connectionState == HubConnectionState.CONNECTED) {
                chatHubConnection?.invoke("LeaveChatRoom", chatRoomId)
                Log.d("SignalRManager", "Left chat room: $chatRoomId")
            }
        } catch (e: Exception) {
            Log.e("SignalRManager", "Error leaving chat room: ${e.message}")
        }
    }

    fun stopConnection() {
        if (hubConnection?.connectionState == HubConnectionState.CONNECTED) {
            hubConnection?.stop()
        }
        if (chatHubConnection?.connectionState == HubConnectionState.CONNECTED) {
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
