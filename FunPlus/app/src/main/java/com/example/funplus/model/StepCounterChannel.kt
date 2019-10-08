package com.example.funplus.model

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

class StepCounterChannel : Application() {
    public val CHANNEL_ID = "serviceChannelID"

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(TAG, "NetworkingChannel onCreate startForegroundService")
            this.startForegroundService(Intent(this, StepCounterService::class.java))
        } else {
            Log.d(TAG, "NetworkingChannel onCreate startService")
            this.startService(Intent(this, StepCounterService::class.java))
        }
        createNotificationChannel()
    }

    /**
     * Checks and creates a notification channel for the step counter if the device
     * SDK is above Android Oreo.
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Step counter",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(serviceChannel)
        }
    }
}