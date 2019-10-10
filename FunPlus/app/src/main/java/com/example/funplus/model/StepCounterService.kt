package com.example.funplus.model

import android.app.PendingIntent
import android.app.Service
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.funplus.R
import com.example.funplus.control.MainActivity
import com.example.funplus.control.TAG
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

const val STEP_COUNT_INTENT = "step count intent"
const val STEP_COUNT_DATA = "step count data"
class StepCounterService : Service(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var stepCountSensor: Sensor? = null

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        Log.d(TAG, "onAccuracyChanged")
    }

    override fun onSensorChanged(p0: SensorEvent) {
        if (p0.sensor == stepCountSensor) {
            val steps = p0.values[0].toInt()
            broadcastStepCount(steps)
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    /**
     *Sets values for the foreground service notification
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(intent?.action.equals("STOP")) {
            stopSelf()
        }
        //Defines the activity to be started when the notification is clicked
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        //Intent for stopping the service when the stopStepcountService service button is clicked
        val stopIntent = Intent(this, StepCounterService::class.java)
        stopIntent.setAction("STOP")
        val pendingStopIntent = PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_CANCEL_CURRENT)

        val notificationChannel = StepCounterChannel()

        //Sets the text/icon for the notification
        val notification =
            NotificationCompat.Builder(this, notificationChannel.CHANNEL_ID)
                .setContentTitle("Counting steps")
                .setSmallIcon(R.drawable.ic_directions)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_error,"Stop counting",pendingStopIntent)
                .build()
            startForeground(1, notification)
            //Restarts the service if closed and passes the previous intent
            return START_REDELIVER_INTENT
    }

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        sensorManager.registerListener(this, stepCountSensor, SensorManager.SENSOR_DELAY_FASTEST)
    }


    fun broadcastStepCount(stepCount: Int) {
        val intent = Intent(STEP_COUNT_INTENT)
        intent.putExtra(STEP_COUNT_DATA, stepCount)
        LocalBroadcastManager.getInstance(this@StepCounterService).sendBroadcast(intent)
    }
}