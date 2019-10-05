package com.example.funplus.model

import android.app.Activity
import android.content.Context
import android.util.Log
import com.example.funplus.control.TAG
import com.example.funplus.utility.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


class UserLocation{
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    /**
     * Gets the last known location of the device
     * @param activity To use fusedLocationClient in a non-activity class
     */
    fun getLocation(activity: Activity, context: Context): Pair<Double, Double> {
        PermissionChecker.askForPermissionIfNotGranted(
            context,
            activity,
            ACCESS_COARSE_LOCATION_REQUEST_CODE,
            ACCESS_COARSE_LOCATION
        )
        PermissionChecker.askForPermissionIfNotGranted(
            context,
            activity,
            ACCESS_FINE_LOCATION_REQUEST_CODE,
            ACCESS_FINE_LOCATION
        )
        var currentLat = 0.0
        var currentLong = 0.0
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        //Gets the last known location with latitude and longitude when task is completed
        fusedLocationClient.lastLocation.addOnCompleteListener{
                task ->
            if(task.isSuccessful && task.result != null) {
                currentLat = task.result!!.latitude
                currentLong = task.result!!.longitude
                Log.d(TAG+ "GeoLoc", "${currentLat}")
                Log.d(TAG+ "GeoLoc", "${currentLong}")
            }else{
                Log.d(TAG+" task.isSuccessful: ", task.isSuccessful.toString())
                Log.d(TAG+" task.result != null: ", task.result.toString())
            }
        }
        return Pair(currentLat, currentLong)
    }
}