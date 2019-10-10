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
import kotlinx.android.synthetic.main.activity_main.*
import android.view.View
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import android.view.animation.DecelerateInterpolator
import android.animation.ObjectAnimator
import android.os.PersistableBundle
import androidx.lifecycle.ViewModelProviders
import com.example.jamie.parentplus.utility.GeoPicDB
import com.example.jamie.parentplus.utility.GeoPicData
import com.example.jamie.parentplus.utility.GeoPicModel
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*
import androidx.lifecycle.Observer


const val TAG = "PARENTPLUS"
const val INTENT_BROADCAST_GEOTAGGEDPICTURE_CHANGED = "geo tagged picture changed in server"
const val INTENT_EXTRA_GEOTAGGEDPICTURE_DATA = "geo tagged picture data"

class MainActivity : AppCompatActivity() {
    private lateinit var geoPicDB: GeoPicDB
    private lateinit var mapFrag: MapFrag
    private lateinit var photoFrag: PhotoFrag
    private lateinit var fTransaction: FragmentTransaction
    private lateinit var fManager: FragmentManager
    private lateinit var animation: ObjectAnimator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fManager = supportFragmentManager
        mapFrag = MapFrag()
        photoFrag = PhotoFrag()
        geoPicDB = GeoPicDB.get(this)

        setupProgressCircle()

        LocalBroadcastManager.getInstance(this).registerReceiver(
            broadCastReceiver,
            IntentFilter(INTENT_BROADCAST_GEOTAGGEDPICTURE_CHANGED)
        )

        showLatestDataFromDB()

        mapBtn.setOnClickListener {
            goToFrag(mapFrag)
        }

        photoBtn.setOnClickListener {
            goToFrag(photoFrag)
        }
    }

    //set up and show a progress circle when data downloading is in progress
    private fun setupProgressCircle() {
        animation = ObjectAnimator.ofInt(
            progressCircle,
            "data downloading in progress",
            0,
            5000
        )
        animation.duration = 50000 // in milliseconds
        animation.interpolator = DecelerateInterpolator()
        animation.start()
        Toast.makeText(this, "downloading new data, please wait", Toast.LENGTH_LONG).show()
    }

    //receive downloaded data from background thread through broadcast
    val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            when (intent?.action) {
                INTENT_BROADCAST_GEOTAGGEDPICTURE_CHANGED -> {
                    Log.d(TAG, "MainActivity has received new data")
                    val data = intent.getStringExtra(INTENT_EXTRA_GEOTAGGEDPICTURE_DATA)
                    if (data == null || data.isEmpty()) {
                        Toast.makeText(
                            this@MainActivity,
                            "Failed to receive data",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Log.d(TAG, "new data size is " + data.length)
                        storeDataInDB(data)
                    }
                }
            }
        }
    }

    private fun storeDataInDB(data: String) {
        doAsync {
            geoPicDB.geoPicDao().insert(GeoPicData(0, data, Date()))
            uiThread {
                passDownloadResult(data)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadCastReceiver)
    }

    //switch between different fragments based on button click
    private fun goToFrag(frag: Fragment) {
        fTransaction = fManager.beginTransaction()
        fTransaction.replace(R.id.fcontainer, frag)
        fTransaction.commit()
    }

    //data is ready, pass to map and photo fragments, and hide progress circle
    fun passDownloadResult(result: String) {
        getLocDataPassToMap(result)
        getImageString(result)
        progressCircle.clearAnimation()
        progressCircle.visibility = View.INVISIBLE
    }


    //read the downloaded file and extract the bitmap string of the picture and pass to photo fragment
    private fun getImageString(input: String) {
        val imgString = input.substringAfter("bitmap=").replace(" ", "+").replace("\n", "")
        val bundle = Bundle()
        bundle.putString("imgString", imgString)
        photoFrag.arguments = bundle
        photoBtn.visibility = View.VISIBLE
    }

    //read the downloaded file and extract the location info and pass to map fragment
    private fun getLocDataPassToMap(result: String) {
        val lines = result.lines()
        val latitude = lines[0].split("=")[1].toDouble()
        val longitude = lines[1].split("=")[1].toDouble()
        val bundle = Bundle()
        bundle.putDouble("latitude", latitude)
        bundle.putDouble("longitude", longitude)
        mapFrag.arguments = bundle
        mapBtn.visibility = View.VISIBLE
    }


    private fun showLatestDataFromDB() {
        val model = ViewModelProviders.of(this).get(GeoPicModel::class.java)
        model.getAllGeoPic().observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                passDownloadResult(it.last().data)
            } else {
                Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show()
            }
        })
    }


}