package com.example.funplus.control

import android.app.Activity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


class UserLocation{
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    var currentLat: Double = 0.0
    var currentLong: Double = 0.0
    /**
     * Gets the last known location of the device
     * @param activity To use fusedLocationClient in a non-activity class
     */
    fun getLocation(activity: Activity) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        //Gets the last known location with latitude and longitude when task is completed
        fusedLocationClient.lastLocation.addOnCompleteListener{
                task ->
            if(task.isSuccessful && task.result != null) {
                currentLat = task.result!!.latitude
                currentLong = task.result!!.longitude
            }
        }
    }
}