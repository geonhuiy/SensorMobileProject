package com.example.jamie.parentplus.control

import android.content.ContentValues.TAG
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class DownloadTask(private val mainActivity: MainActivity, private val url: URL) :
    AsyncTask<Unit, Unit, String>() {

    override fun doInBackground(vararg params: Unit): String {
        val conn = url.openConnection() as HttpURLConnection
        conn.connect()
        val inputStream: InputStream = conn.getInputStream()
        val allText = inputStream.bufferedReader().use {
            it.readText()
        }
        val result = StringBuilder()
        result.append(allText)
        val str = result.toString()
        inputStream.close()
        return str

    }

    //when task is done
    override fun onPostExecute(result: String?) {
        Log.d(TAG + " File.txt", result!!)
        if (!result.isNullOrEmpty()) {
            mainActivity.getDownloadResult(result)
        } else {
            Toast.makeText(
                mainActivity.applicationContext,
                "download result empty",
                Toast.LENGTH_LONG
            ).show()
        }
    }


}
