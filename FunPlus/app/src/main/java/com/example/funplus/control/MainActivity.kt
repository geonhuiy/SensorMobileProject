package com.example.funplus.control

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.funplus.R
import com.example.funplus.model.Picture
import com.example.funplus.model.PictureUpload
import com.example.funplus.model.UserLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream

const val TAG = "DBG"

class MainActivity : AppCompatActivity() {
    private lateinit var plusMinusFrag: PlusMinusFrag
    private lateinit var letterFrag: LetterFrag
    private lateinit var sosFrag: SosFrag
    private lateinit var fTransaction: FragmentTransaction
    private lateinit var fManager: FragmentManager

    private val userPicture = Picture()

    var currentLat: Double = 0.0
    var currentLong: Double = 0.0
    var userLoc = UserLocation()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fManager = supportFragmentManager
        showPlusMinusFrag()
        plusMinusFrag = PlusMinusFrag()
        letterFrag = LetterFrag()
        sosFrag = SosFrag()

        goToNumberGameBtn.setOnClickListener {
            goToGameFrag(plusMinusFrag)
        }
        goToLetterGameBtn.setOnClickListener {
            goToGameFrag(letterFrag)
        }
        /*sosBtn.setOnClickListener {
            goToGameFrag(sosFrag)
        }*/
        fab_sos.setOnClickListener {
            userPicture.takePicture(this, this)
            userLoc.getLocation(this)
        }
    }

    //display plus-minus game by default when app starts
    private fun showPlusMinusFrag() {
        Log.d(TAG, "showPlusMinusFrag()")
        plusMinusFrag = PlusMinusFrag()
        fTransaction = fManager.beginTransaction()
        fTransaction.add(R.id.fcontainer, plusMinusFrag)
        fTransaction.commit()
    }

    //switch between different fragments
    private fun goToGameFrag(frag: Fragment) {
        Log.d(TAG, "goToGameFrag")
        fTransaction = fManager.beginTransaction()
        fTransaction.replace(R.id.fcontainer, frag)
        //fTransaction.addToBackStack(null)
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
        if (requestCode == userPicture.captureReq && resultCode == Activity.RESULT_OK) {
            //Gets a bitmap from picture taken with camera
            val imgBitmap = BitmapFactory.decodeFile(userPicture.photoPath)
            val byteArrayOutputStream = ByteArrayOutputStream()
            imgBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            val bitmapString = Base64.encodeToString(byteArray, Base64.DEFAULT)
            Log.d("Bitmap", bitmapString)
            currentLat = userLoc.currentLat
            currentLong = userLoc.currentLong
            val uploadPicture = PictureUpload(this)
            uploadPicture.upload(currentLat, currentLong, bitmapString)
            Log.d("CurrentLat", currentLat.toString())
            Log.d("Photopath", userPicture.photoPath)
        }
    }

}
