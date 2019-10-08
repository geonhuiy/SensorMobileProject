package com.example.jamie.parentplus.control.utility

import android.util.Log
import com.example.jamie.parentplus.control.TAG
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class NewDataChecker () {
    fun checkLastModifiedTime(service:NetworkingService) {
        //check the time the file(whrere user location and picture are stored) was last modified
        // (if it was modified later than the previous time, it means
        // the file was updated -> new content was uploaded)
        doAsync {
            val url = URL("")
            val conn = url.openConnection() as HttpURLConnection
            conn.connect()
            val inputStream: InputStream = conn.getInputStream()
            val allText = inputStream.bufferedReader().use {
                it.readText()
            }
            val result = StringBuilder()
            result.append(allText)
            val resultString = result.toString()
            inputStream.close()

            //get result, and inform service about the time
            uiThread {
                Log.d(TAG, " checkLastModifiedTime finished, result: " + resultString)
                val currentTime = resultString.substringAfterLast("last changed: ")
                Log.d(TAG, "lastModifiedTime: " + currentTime)
                service.isFileChanged(currentTime)
            }
        }
    }

}