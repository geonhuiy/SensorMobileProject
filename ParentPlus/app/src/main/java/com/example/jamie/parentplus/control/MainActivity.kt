package com.example.jamie.parentplus.control

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.jamie.parentplus.R
import com.example.jamie.parentplus.control.utility.NetworkingService
import kotlinx.android.synthetic.main.activity_main.*
import android.os.Build
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager


const val TAG = "PARENTPLUS"
const val INTENT_BROADCAST_GEOTAGGEDPICTURE_CHANGED = "geo tagged picture changed in server"
const val INTENT_EXTRA_GEOTAGGEDPICTURE_DATA = "geo tagged picture data"

/*class Foo {

    companion object {
        lateinit var instance: Foo
        lateinit var mainActivityInstance: MainActivity

        fun setMainActivity(activity:MainActivity) {
            mainActivityInstance = activity
        }

        fun getMainActivity() :MainActivity{
            return mainActivityInstance
        }
    }

    init {
        instance = this
    }
}*/
class MainActivity : AppCompatActivity() {
    private lateinit var mapFrag: MapFrag
    private lateinit var photoFrag: PhotoFrag
    private lateinit var fTransaction: FragmentTransaction
    private lateinit var fManager: FragmentManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocalBroadcastManager.getInstance(this).registerReceiver(
            broadCastReceiver,
            IntentFilter(INTENT_BROADCAST_GEOTAGGEDPICTURE_CHANGED)
        )

        setContentView(R.layout.activity_main)
        fManager = supportFragmentManager
        mapFrag = MapFrag()
        photoFrag = PhotoFrag()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.startForegroundService(Intent(this, NetworkingService::class.java))
        } else {
            this.startService(Intent(this, NetworkingService::class.java))
        }


        mapBtn.setOnClickListener {
            goToFrag(mapFrag)
        }

        photoBtn.setOnClickListener {
            goToFrag(photoFrag)
        }
    }

    val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            when (intent?.action) {
                INTENT_BROADCAST_GEOTAGGEDPICTURE_CHANGED -> {
                    Log.d(TAG, "MainActivity has received new data")
                    val data = intent.getStringExtra(INTENT_EXTRA_GEOTAGGEDPICTURE_DATA)
                    if (data == null || data.isEmpty()) {
                        Toast.makeText(contxt, "new data null or empty", Toast.LENGTH_LONG).show()
                        Log.d(TAG, "new data null or empty")
                    } else {
                        Log.d(TAG, "   picture size is " + data.length)
                        getDownloadResult(data)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadCastReceiver)

    }

    //switch between different fragments
    private fun goToFrag(frag: Fragment) {
        Log.d(TAG, "goToFrag " + frag)
        fTransaction = fManager.beginTransaction()
        fTransaction.replace(R.id.fcontainer, frag)
        fTransaction.commit()
    }


    fun getDownloadResult(result: String) {
        Log.d(TAG + "getResult: ", result)
        getLocDataPassToMap(result)
        getImageString(result)
        mapBtn.isEnabled = true
        photoBtn.isEnabled = true
    }


    //read the downloaded file and extract the bitmap string of the picture and pass to photo fragment
    private fun getImageString(input: String) {
        val imgString = input.substringAfter("bitmap=").replace(" ", "+").replace("\n", "")
        val bundle = Bundle()
        bundle.putString("imgString", imgString)
        photoFrag.arguments = bundle
    }

    //read the downloaded file and extract the location info and pass to map fragment
    private fun getLocDataPassToMap(result: String) {
        val lines = result.lines()
        Log.d(TAG + "line1: ", lines[0])
        Log.d(TAG + "line2: ", lines[1])
        val latitude = lines[0].split("=")[1].toDouble()
        val longitude = lines[1].split("=")[1].toDouble()
        val bundle = Bundle()
        bundle.putDouble("latitude", latitude)
        bundle.putDouble("longitude", longitude)
        Log.d(TAG + " latitude1: ", latitude.toString())
        Log.d(TAG + " longitude1: ", longitude.toString())
        mapFrag.arguments = bundle
    }
}