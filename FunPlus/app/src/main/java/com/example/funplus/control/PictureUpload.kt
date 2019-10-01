package com.example.funplus.control

import android.content.Context
import android.net.ConnectivityManager
import com.google.gson.Gson
import org.jetbrains.anko.doAsync
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class PictureUpload(context: Context) {
    private val uploadContext = context
    //URL to be removed
    private val uploadUrl = URL("https://users.metropolia.fi/~geonhuiy/FunPlus/location")

    object UploadJSON {
        data class sosData(val lat: String, val long: String, val imageBitmap: String)
    }

    /**
     * Uploads a location (latitude and longitude) with a Base64 string of an image bitmap as JSON to
     * a defined URL
     * @param lat Latitude of current location
     * @param long Longitude of current location
     * @param bitmap Base64 string of bitmap
     */
    fun upload(lat: Double, long: Double, bitmap: String) {
        doAsync {
            //HTTPConnection setup
            val conn = uploadUrl.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.doOutput = true
            val outputStream = conn.outputStream
            //Creates an object of a JSON with data from param
            val newLoc = UploadJSON.sosData(
                lat.toString(),
                long.toString(),
                bitmap
            )
            //Builds a JSON from the object created above
            val gson = Gson()
            val json = gson.toJson(newLoc)
            //Overwrites content in the defined .php file
            outputStream.bufferedWriter().use {
                it.write(json.toString())
            }
            //Reads the content of the uploaded JSON
            val inputStream: InputStream =
                conn.getInputStream()
            val allText = inputStream.bufferedReader().use {
                it.readText()
            }
            val result = StringBuilder()
            result.append(allText)
            val str = result.toString()
            //Log.d("Upload", str)
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val cm = uploadContext.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val networkInfo = cm.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}