package com.example.jamie.parentplus.control

import android.content.Context
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.jamie.parentplus.R
import kotlinx.android.synthetic.main.activity_main.*
import org.osmdroid.config.Configuration
import java.net.URL

const val TAG = "PARENTPLUS"

class MainActivity : AppCompatActivity() {
    private lateinit var mapFrag: MapFrag
    private lateinit var photoFrag: PhotoFrag
    private lateinit var fTransaction: FragmentTransaction
    private lateinit var fManager: FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fManager = supportFragmentManager
        mapFrag = MapFrag()
        photoFrag = PhotoFrag()

        if (isNetworkAvailable()) {
            val imgFileUrl = URL("https://users.metropolia.fi/~youqins/uploads/sosFile.txt")
            val locFileUrl = URL("https://users.metropolia.fi/~youqins/uploads/location.txt")
            DownloadTask(this, imgFileUrl).execute()
            //DownloadTask(this, locFileUrl).execute()
        }

        mapBtn.setOnClickListener {
            goToFrag(mapFrag)
        }

        photoBtn.setOnClickListener {
            goToFrag(photoFrag)
        }
    }


    private fun isNetworkAvailable(): Boolean {
        val cm = this.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        return cm.activeNetworkInfo?.isConnected == true
    }


    //switch between different fragments
    private fun goToFrag(frag: Fragment) {
        Log.d(TAG, "goToFrag " + frag)
        fTransaction = fManager.beginTransaction()
        fTransaction.replace(R.id.fcontainer, frag)
        fTransaction.commit()
    }


    fun getDownloadResult(result: String) {
        val lines = result.lines()
        Log.d(TAG+"line1: ", lines[0])
        Log.d(TAG+"line2: ", lines[1])

        getLocDataPassToMap(lines)

        getImageString(lines)

    }

    private fun getImageString(lines: List<String>) {
        var imgString = ""
        for (i in 2..lines.lastIndex) {
            imgString += lines[i]
        }
        imgString = imgString.replace("\\s+"," ")
        Log.d(TAG, imgString)

        val bundle = Bundle()
        bundle.putString("imgString", imgString)
        photoFrag.arguments = bundle
    }

    //pass the location data to map fragment
    private fun getLocDataPassToMap(lines: List<String>) {
        val latitude = lines[0].split("=")[1].toDouble()
        val longitude = lines[1].split("=")[1].toDouble()
        val bundle = Bundle()
        bundle.putDouble("latitude", latitude)
        bundle.putDouble("longitude", longitude)
        Log.d(TAG+" latitude1: ", latitude.toString())
        Log.d(TAG+" longitude1: ", longitude.toString())
        mapFrag.arguments = bundle
    }
}
