package com.example.funplus.model

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class StepCounterChannel : Application() {
     val CHANNEL_ID = "serviceChannelID"

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "step counter service channel onCreate() ENTER")
        startStopService()
        createNotificationChannel()
    }

    /*
     *start and stopStepcountService service based on predefined time and sdk version
     */
    private fun startStopService() {
        val start = checkStartStopTime().first
        val stop = checkStartStopTime().second
        Log.d(TAG, "step counter service channel startStopService() start = "+start + "stopStepcountService = "+stop)
        if (start) {
            Log.d(TAG, "current time is between 8:00 - 17:00")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.d(TAG, "NetworkingChannel onCreate startForegroundService")
                this.startForegroundService(Intent(this, StepCounterService::class.java))
            }else{
                Log.d(TAG, "NetworkingChannel onCreate startService")
                this.startService(Intent(this, StepCounterService::class.java))
            }
        } else if (stop) {
            Log.d(TAG, "time has passed 17:00, step counter service stopped")
            this.stopService(Intent(this, StepCounterService::class.java))
        }
    }

    /**
     * compare current time and predefined time(run step counter service from 8:00 - 17:00)
     * to start and stopStepcountService service
     */
    private fun checkStartStopTime(): Pair<Boolean, Boolean> {
        Log.d(TAG, "checkStartStopTime ENTER")
        val timeNow = getTimeNow()
        var start = false
        var stop = false
        var serviceStarted = false
        if (timeNow >= "08:00" && timeNow < "22:00") {
            Log.d(TAG, "current time is between 8:00 - 17:00, start step counter service")
            start = true
        } else if (timeNow >= "22:00") {
            Log.d(TAG, "time has passed 17:00, stop step counter service ")
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
            Log.d(TAG, "step counter service current time: " + timeNow)
        }
        return timeNow
    }




    /**
     * Checks and creates a notification channel for the step counter if the device
     * SDK is above Android Oreo.
     */
    private fun createNotificationChannel() {
        Log.d(TAG, "createNotificationChannel")
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