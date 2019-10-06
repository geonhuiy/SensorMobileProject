package com.example.funplus.utility

import android.util.Log
import com.example.funplus.control.TAG
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class Uploader() {

    /**
     * upload location info and image bitmap (converted to String) using https
     */
    companion object {
        fun doUpload(latitude: String, longitute: String, imgBitmap: String) {
            val fileData = "lat=${latitude}\nlong=${longitute}\nbitmap=${imgBitmap}"
            val fileName = "sosFile.txt"
            val message = "filename=$fileName&file-data=$fileData&submit=Upload+Image"
            doAsync {
                Log.d(TAG, " doUpload doAsync")
                val url = URL("https://users.metropolia.fi/~youqins/upload_textform.php")
                val urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.requestMethod = "POST"
                urlConnection.doOutput = true
                val outputStream = urlConnection.getOutputStream()
                outputStream.bufferedWriter()
                    .use { it.write(message) }

                val istream: InputStream = urlConnection.getInputStream()
                val allText = istream.bufferedReader().use { it.readText() }
                val result = StringBuilder()
                result.append(allText)

                uiThread {
                    Log.d(TAG + " doUpload doAsync result: ", result.toString())
                }
            }
        }
    }
}

