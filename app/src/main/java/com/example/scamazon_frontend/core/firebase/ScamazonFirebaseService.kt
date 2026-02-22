package com.example.scamazon_frontend.core.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.scamazon_frontend.MainActivity
import com.example.scamazon_frontend.R
import com.example.scamazon_frontend.core.network.RetrofitClient
import com.example.scamazon_frontend.core.utils.TokenManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ScamazonFirebaseService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "ScamazonFCM"
        private const val CHANNEL_ID = "scamazon_notifications"
        private const val CHANNEL_NAME = "Scamazon Notifications"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM token: $token")
        // Send token to backend
        sendTokenToServer(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d(TAG, "Message received: ${message.data}")

        val title = message.notification?.title ?: message.data["title"] ?: "Scamazon"
        val body = message.notification?.body ?: message.data["body"] ?: ""

        if (body.isNotBlank()) {
            showNotification(title, body)
        }
    }

    private fun showNotification(title: String, body: String) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel (required for Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Thông báo từ Scamazon"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Create intent to open app when notification is tapped
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun sendTokenToServer(token: String) {
        val tokenManager = TokenManager(applicationContext)
        val authToken = tokenManager.getToken()
        if (authToken.isNullOrEmpty()) {
            Log.d(TAG, "No auth token - skipping FCM registration")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val retrofit = RetrofitClient.getClient(applicationContext)
                val authService = retrofit.create(com.example.scamazon_frontend.data.remote.AuthService::class.java)
                val response = authService.saveFcmToken(
                    mapOf("token" to token, "deviceType" to "android")
                )
                if (response.isSuccessful) {
                    Log.d(TAG, "FCM token sent to server successfully")
                } else {
                    Log.e(TAG, "Failed to send FCM token: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error sending FCM token: ${e.message}")
            }
        }
    }
}
