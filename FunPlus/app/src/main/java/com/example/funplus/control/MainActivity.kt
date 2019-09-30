package com.example.funplus.control

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.funplus.R
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

const val TAG = "DBG"
class MainActivity : AppCompatActivity() {
    private lateinit var plusMinusFrag: PlusMinusFrag
    private lateinit var letterFrag: LetterFrag
    private lateinit var sosFrag: SosFrag
    private lateinit var fTransaction: FragmentTransaction
    private lateinit var fManager: FragmentManager

    private val userLocation = UserLocation()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fManager = supportFragmentManager
        showPlusMinusFrag()
        plusMinusFrag = PlusMinusFrag()
        letterFrag = LetterFrag()
        sosFrag = SosFrag()

        goToNumberGameBtn.setOnClickListener{
            goToGameFrag(plusMinusFrag)
        }
        goToLetterGameBtn.setOnClickListener{
            goToGameFrag(letterFrag)
        }
        sosBtn.setOnClickListener {
            goToGameFrag(sosFrag)
        }
        fab_sos.setOnClickListener {
            userLocation.takePicture(this,this)
        }
    }


    private fun showPlusMinusFrag() {
        Log.d(TAG, "showPlusMinusFrag()")
        plusMinusFrag = PlusMinusFrag()
        fTransaction = fManager.beginTransaction()
        fTransaction.add(R.id.fcontainer, plusMinusFrag)
        fTransaction.commit()
    }

    private fun goToGameFrag(frag: Fragment) {
        Log.d(TAG, "goToGameFrag")
        fTransaction = fManager.beginTransaction()
        fTransaction.replace(R.id.fcontainer, frag)
        //fTransaction.addToBackStack(null)
        fTransaction.commit()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == userLocation.captureReq && resultCode == Activity.RESULT_OK) {
            //Gets a bitmap from picture taken with camera
            val imgBitmap = BitmapFactory.decodeFile(userLocation.photoPath)
            Log.d("Photopath", userLocation.photoPath)
        }
    }


}
