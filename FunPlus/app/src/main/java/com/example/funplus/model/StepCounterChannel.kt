package com.example.funplus.model

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Build
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class StepCounterChannel : Application() {
     val CHANNEL_ID = "serviceChannelID"

    override fun onCreate() {
        super.onCreate()
        startStopService()
        createNotificationChannel()
    }

    /*
     *start and stopStepcountService service based on predefined time and sdk version
     */
    private fun startStopService() {
        val start = checkStartStopTime().first
        val stop = checkStartStopTime().second
        if (start) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                this.startForegroundService(Intent(this, StepCounterService::class.java))
            }else{
                this.startService(Intent(this, StepCounterService::class.java))
            }
        } else if (stop) {
            this.stopService(Intent(this, StepCounterService::class.java))
        }
    }

    /**
     * compare current time and predefined time(run step counter service from 8:00 - 22:00)
     * to start and stopStepcountService service
     */
    private fun checkStartStopTime(): Pair<Boolean, Boolean> {
        val timeNow = getTimeNow()
        var start = false
        var stop = false
        if (timeNow >= "08:00" && timeNow < "22:00") {
            start = true
        } else if (timeNow >= "22:00") {
            stop = true
        }
        return Pair(start, stop)
    }

    //get current time in hour and minute in string format
    private fun getTimeNow(): String {
        var timeNow = ""
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val dateTimeNow = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            timeNow = dateTimeNow.format(formatter)
        }
        return timeNow
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

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(serviceChannel)
        }
    }
}