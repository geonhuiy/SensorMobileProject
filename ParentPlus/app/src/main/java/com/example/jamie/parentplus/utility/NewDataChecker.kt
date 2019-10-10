package com.example.jamie.parentplus.control.utility

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
            val url = URL("https://users.metropolia.fi/~youqins/uploads/lastModifideTime.php")
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
                val currentTime = resultString.substringAfterLast("last changed: ")
                service.isFileChanged(currentTime)
            }
        }
    }

}