package com.example.scamazon_frontend.core.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.scamazon_frontend.MainActivity
import com.example.scamazon_frontend.R

/**
 * Helper class to show/cancel cart badge on the app launcher icon.
 * Uses NotificationCompat to create a silent notification that triggers
 * a badge count on the app icon (Android 8.0+).
 */
object CartBadgeNotificationHelper {

    private const val CHANNEL_ID = "cart_badge"
    private const val CHANNEL_NAME = "Cart Badge"
    private const val NOTIFICATION_ID = 9999

    /**
     * Show a low-priority notification to trigger a badge on the app icon.
     * The notification appears in the shade (not as a popup banner).
     */
    fun showCartBadge(context: Context, count: Int) {
        if (count <= 0) return

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel (Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW  // No popup, shows in shade only
            ).apply {
                description = "Cart item count badge on app icon"
                setShowBadge(true)
                enableVibration(false)
                setSound(null, null)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Intent to open app when tapped
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Scamazon")
            .setContentText("You have $count item${if (count > 1) "s" else ""} in your cart")
            .setNumber(count)
            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
            .setSilent(true)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    /**
     * Cancel the cart badge notification (call when app returns to foreground).
     */
    fun cancelCartBadge(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
    }
}
