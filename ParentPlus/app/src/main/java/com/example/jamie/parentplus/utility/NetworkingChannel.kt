package com.example.jamie.parentplus.control.utility

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.content.Intent
import com.example.jamie.parentplus.control.TAG


class NetworkingChannel : Application() {
    val CHANNEL_ID = "serviceChannelID"

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.startForegroundService(Intent(this, NetworkingService::class.java))
        } else {
            this.startService(Intent(this, NetworkingService::class.java))
        }
        createNotificationChannel()
    }

    /**
     * Checks and creates a notification channel for new data upload if the device
     * SDK is above Android Oreo.
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "check new upload",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(serviceChannel)
        }
    }
}