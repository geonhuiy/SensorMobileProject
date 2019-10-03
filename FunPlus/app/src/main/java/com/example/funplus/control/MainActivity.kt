package com.example.funplus.control

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.funplus.R
import com.example.funplus.model.Picture
import com.example.funplus.model.UserLocation
import com.example.funplus.utility.*
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream

const val TAG = "DBG"



class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var plusMinusFrag: NumberFrag
    private lateinit var letterFrag: LetterFrag
    private lateinit var fTransaction: FragmentTransaction
    private lateinit var fManager: FragmentManager
    private lateinit var userPicture: Picture
    private lateinit var userLoc: UserLocation

    private lateinit var sensorManager: SensorManager
    private var stepCountSensor: Sensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        fManager = supportFragmentManager
        showPlusMinusFrag()
        plusMinusFrag = NumberFrag()
        letterFrag = LetterFrag()

        goToNumberGameBtn.setOnClickListener {
            goToGameFrag(plusMinusFrag)
        }
        goToLetterGameBtn.setOnClickListener {
            goToGameFrag(letterFrag)
        }

        fab_sos.setOnClickListener {
            userPicture = Picture()
            userPicture.takePicture(this, this)
            userLoc = UserLocation()
        }
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, stepCountSensor, SensorManager.SENSOR_DELAY_FASTEST)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        //Do something
    }

    override fun onSensorChanged(p0: SensorEvent) {
        if (p0.sensor == stepCountSensor) {
            //goToLetterGameBtn.text = p0.values[0].toString()
            Log.d("Steps",p0.values[0].toString() )
        }
    }
    
    //display plus-minus game by default when app starts
    private fun showPlusMinusFrag() {
        Log.d(TAG, "showPlusMinusFrag()")
        plusMinusFrag = NumberFrag()
        fTransaction = fManager.beginTransaction()
        fTransaction.add(R.id.fcontainer, plusMinusFrag)
        fTransaction.commit()
    }

    //switch between different fragments
    private fun goToGameFrag(frag: Fragment) {
        Log.d(TAG, "goToGameFrag")
        fTransaction = fManager.beginTransaction()
        fTransaction.replace(R.id.fcontainer, frag)
        fTransaction.commit()
    }

    /**
     * Function runs when the user successfully takes a picture and confirms. Bitmap of the picture
     * is converted into a Base64 string to be used for upload, along with the location data.
     * @param requestCode Request from previous activity
     * @param resultCode Checks if previous operation was initiated successfully, RESULT_OK ensures
     *                   that a picture was taken successfully.
     * @param data Data from the previous activity
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        PermissionChecker.askForPermissionIfNotGranted(this, this, CAMERA_REQUEST_CODE, CAMERA)
        if (requestCode == userPicture.captureReq && resultCode == Activity.RESULT_OK) {
            //Gets a bitmap from picture taken with camera
            val imgBitmap = BitmapFactory.decodeFile(userPicture.photoPath)
            val byteArrayOutputStream = ByteArrayOutputStream()
            imgBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            val bitmapString = Base64.encodeToString(byteArray, Base64.DEFAULT)

            Log.d("Bitmap", bitmapString)

            doUpload(bitmapString)

        }
    }

    private fun doUpload(bitmapString: String) {
        Log.d(TAG, "doUpload")
        val location = userLoc.getLocation(this, this)
        Log.d(TAG + " CurrentLoc: ", location.toString())
        Log.d(TAG + "Photopath", userPicture.photoPath)
        PermissionChecker.askForPermissionIfNotGranted(this, this, INTERNET_REQUEST_CODE, INTERNET)
        PermissionChecker.askForPermissionIfNotGranted(this, this, ACCESS_NETWORK_STATE_REQUEST_CODE, ACCESS_NETWORK_STATE)
        Uploader.doUpload(location.first.toString(), location.second.toString(), bitmapString)
    }

}
