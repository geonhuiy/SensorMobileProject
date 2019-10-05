package com.example.funplus.utility

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.funplus.control.TAG

const val RECORD_AUDIO_REQUEST_CODE = 10
const val INTERNET_REQUEST_CODE = 11
const val WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 12
const val ACCESS_FINE_LOCATION_REQUEST_CODE = 13
const val ACCESS_COARSE_LOCATION_REQUEST_CODE = 14
const val ACCESS_NETWORK_STATE_REQUEST_CODE = 15
const val CAMERA_REQUEST_CODE = 16
const val CAMERA_AR_REQUEST_CODE = 17
const val ACCESS_COARSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION
const val ACCESS_FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION
const val CAMERA = android.Manifest.permission.CAMERA
const val RECORD_AUDIO = android.Manifest.permission.RECORD_AUDIO
const val INTERNET = android.Manifest.permission.INTERNET
const val ACCESS_NETWORK_STATE = android.Manifest.permission.ACCESS_NETWORK_STATE

object PermissionChecker{
    fun askForPermissionIfNotGranted(context: Context, activity: Activity, requestCode: Int, request: String) {
            val permission = ContextCompat.checkSelfPermission(context, request)
            if (permission != PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Permission denied, asking for permission")
                ActivityCompat.requestPermissions(activity,
                    arrayOf(request),requestCode)
            }
        }
}