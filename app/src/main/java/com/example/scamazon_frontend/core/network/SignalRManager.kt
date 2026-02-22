package com.example.scamazon_frontend.core.network

import android.content.Context
import android.util.Log
import com.example.scamazon_frontend.core.utils.TokenManager
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

enum class AppEvent {
    OrderUpdated,
    ProductUpdated
}

class SignalRManager private constructor(context: Context) {
    private val tokenManager = TokenManager(context)
    private var hubConnection: HubConnection? = null

    // SharedFlow to emit events to whoever is listening (ViewModels)
    private val _events = MutableSharedFlow<AppEvent>(extraBufferCapacity = 10)
    val events: SharedFlow<AppEvent> = _events.asSharedFlow()

    init {
        // Use the same BASE_URL, but point to the hub
        val url = "http://10.0.2.2:5255/app-hub"

        try {
            hubConnection = HubConnectionBuilder.create(url)
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

            hubConnection?.onClosed { error ->
                Log.e("SignalRManager", "Connection closed. Error: ${error?.message}")
                // In a production app, we could implement retry logic here natively if avoid .withAutomaticReconnect() below
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
                // SignalR uses RxJava underneath
                hubConnection?.start()?.doOnComplete {
                    Log.d("SignalRManager", "Connection started successfully")
                }?.doOnError { e ->
                    Log.e("SignalRManager", "Error starting connection: ${e.message}")
                }?.onErrorComplete()?.subscribe() // fire and forget subscription basically
            } catch (e: Exception) {
                Log.e("SignalRManager", "Connection Exception: ${e.message}")
            }
        }
    }

    fun stopConnection() {
        if (hubConnection?.connectionState == com.microsoft.signalr.HubConnectionState.CONNECTED) {
            hubConnection?.stop()
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
