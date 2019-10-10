package com.example.jamie.parentplus.control.utility

import DownloadTask
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.jamie.parentplus.R
import com.example.jamie.parentplus.control.MainActivity
import android.os.Handler
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.jamie.parentplus.control.TAG


class NetworkingService() : Service() {
    private lateinit var handler: Handler
    lateinit var newDataChecker: NewDataChecker
    val DEFAULT_SYNC_INTERVAL = (30 * 1000).toLong() //checking server for new data every 30 seconds
    var currentTime = "" // time of the last change on server side

    override fun onCreate() {
        super.onCreate()
        handler = Handler()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(applicationContext, NetworkingService::class.java))
    }


    //make http request every 10 seconds to check if there is new change in the file
    private val runnableService = object : Runnable {
        override fun run() {
            newDataChecker = NewDataChecker()
            if (isNetworkAvailable()) {
                newDataChecker.checkLastModifiedTime(this@NetworkingService)
                handler.postDelayed(this, DEFAULT_SYNC_INTERVAL)
            }
        }
    }

    /**
     *Sets values for the foreground service notification
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        handler.post(runnableService)
        val notification: Notification

        //Intent for stopping the service when the stopStepcountService service button is clicked
        val stopIntent = Intent(this, NetworkingService::class.java)
        stopIntent.setAction("STOP")
        val pendingStopIntent = PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_CANCEL_CURRENT)

        //Defines the activity to be started when the notification is clicked
        val notificationIntent = Intent(this, MainActivity::class.java)
        val notificationChannel = NetworkingChannel()
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        //Sets the text/icon for the notification
        notification =
            NotificationCompat.Builder(this, notificationChannel.CHANNEL_ID)
                .setContentTitle("Checking for new data")
                .setContentInfo("New location and photo from FunPlus user")
                .setSmallIcon(R.drawable.ic_error)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_error, "Stop checking for new data", pendingStopIntent)
                .build()
        startForeground(1, notification)
        //Restarts the service if closed and passes the previous intent
        return START_REDELIVER_INTENT
    }

    //compare the previous time and current time when the file was modified,
    // if not same, meaning file has been changed
    fun isFileChanged(newTime: String): Boolean {
        if (newTime != currentTime) {
            currentTime = newTime
            DownloadTask(LocalBroadcastManager.getInstance(this)).execute()
            return true
        }
        return false
    }


    //check if network is available
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

}