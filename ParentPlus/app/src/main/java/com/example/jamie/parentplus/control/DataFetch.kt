package com.example.jamie.parentplus.control

import android.util.Log
import org.jetbrains.anko.doAsync
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class DataFetch {
    private val fetchUrl = URL("https://users.metropolia.fi/~geonhuiy/FunPlus/location")

    fun fetch() {
        doAsync {
            val conn = fetchUrl.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.doOutput = true
            conn.setRequestProperty("Content-Type",
                "application/json;charset=utf-8")
            conn.setRequestProperty("X-Requested-With", "XMLHttpRequest")
            val inputStream: InputStream =
                conn.getInputStream()
            val allText = inputStream.bufferedReader().use {
                it.readText()
            }
            val result = StringBuilder()
            result.append(allText)
            val str = result.toString()
            Log.d("Upload", str)
        }
    }
}