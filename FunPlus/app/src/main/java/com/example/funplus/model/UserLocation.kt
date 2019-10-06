package com.example.funplus.model

import android.app.Activity
import android.content.Context
import android.location.Location
import android.util.Log
import com.example.funplus.control.TAG
import com.example.funplus.utility.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task


class UserLocation{

    /**
     * Gets the last known location of the device
     * @param activity To use fusedLocationClient in a non-activity class
     */
    companion object{
        fun getLocation(activity: Activity, context: Context): Task<Location> {
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

            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
            //return the task which returns the last known location with latitude and longitude when task is completed
            return fusedLocationClient.lastLocation
        }
    }

}