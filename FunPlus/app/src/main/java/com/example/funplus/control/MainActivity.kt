package com.example.funplus.control

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
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
import com.example.funplus.model.StepCounterService


const val TAG = "DBG"

class MainActivity : AppCompatActivity(){

    private lateinit var plusMinusFrag: NumberFrag
    private lateinit var letterFrag: LetterFrag
    private lateinit var dataFrag: NumberGraphFrag
    private lateinit var fTransaction: FragmentTransaction
    private lateinit var fManager: FragmentManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fManager = supportFragmentManager
        plusMinusFrag = NumberFrag()
        letterFrag = LetterFrag()

        dataFrag = NumberGraphFrag()

        showPlusMinusFrag()
        goToNumberGameBtn.setBackgroundColor(resources.getColor(R.color.color5Light))
        //Starts step counter service
        startService(Intent(applicationContext, StepCounterService::class.java))

        goToNumberGameBtn.setOnClickListener {
            goToGameFrag(plusMinusFrag)
            goToNumberGameBtn.setBackgroundColor(resources.getColor(R.color.color5Light))
            goToLetterGameBtn.setBackgroundColor(resources.getColor(R.color.color5))
        }
        goToDataActivityBtn.setOnClickListener {
            goToDataActivity()

        }
        goToLetterGameBtn.setOnClickListener {
            goToGameFrag(letterFrag)
            goToNumberGameBtn.setBackgroundColor(resources.getColor(R.color.color5))
            goToLetterGameBtn.setBackgroundColor(resources.getColor(R.color.color5Light))
        }

        fab_sos.setOnClickListener {
            Picture.takePicture(this, this)
            UserLocation.getLocation(this, this)
        }
    }
    //display plus-minus game by default when app starts
    private fun showPlusMinusFrag() {
        plusMinusFrag = NumberFrag()
        fTransaction = fManager.beginTransaction()
        fTransaction.add(R.id.fcontainer, plusMinusFrag)
        fTransaction.commit()
    }

    //switch between different fragments
    private fun goToGameFrag(frag: Fragment) {
        fTransaction = fManager.beginTransaction()
        fTransaction.replace(R.id.fcontainer, frag)
        fTransaction.commit()
    }

    private fun goToDataActivity(){
        val intent = Intent(this, DataActivity::class.java)
        startActivity(intent)
        this.finish()
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
        if (requestCode == Picture.captureReq && resultCode == Activity.RESULT_OK) {
            //get original bitmap, and resize it to half(for easier upload and download)
            val imgBitmap = BitmapFactory.decodeFile(Picture.photoPath)
            val finalBitmap = resizeBitmap(imgBitmap)
            val byteArrayOutputStream = ByteArrayOutputStream()
            finalBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            val bitmapString = Base64.encodeToString(byteArray, Base64.DEFAULT)
            doUpload(bitmapString)
        }
    }

    /**
     * resize original photo to a smaller size, so it is easier to covert and upload/download
     */
    private fun resizeBitmap(imgBitmap: Bitmap): Bitmap? {
        val oriWidth = imgBitmap.width
        val oriHeight = imgBitmap.height
        val finalWidth = (oriWidth * 0.2).toInt()
        val finalHeight = (oriHeight * 0.2).toInt()
        val finalBitmap = Bitmap.createScaledBitmap(imgBitmap, finalWidth, finalHeight, false)
        return finalBitmap
    }

    /**
     * wait location task complete and get latitude and longitude
     * upload bitmap string and location info to server
     */
    private fun doUpload(bitmapString: String) {
        UserLocation.getLocation(this, this).addOnCompleteListener {
            if (it.isSuccessful && it.result != null) {
                val currentLat = it.result!!.latitude
                val currentLong = it.result!!.longitude
                Uploader.doUpload(currentLat.toString(), currentLong.toString(), bitmapString)
            } else {
                Toast.makeText(this, "failed to get location coordinates", Toast.LENGTH_LONG).show()
            }
        }
    }

}
