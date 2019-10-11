import android.content.Intent
import android.os.AsyncTask
import com.example.jamie.parentplus.control.INTENT_BROADCAST_GEOTAGGEDPICTURE_CHANGED
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.jamie.parentplus.control.INTENT_EXTRA_GEOTAGGEDPICTURE_DATA
import com.example.jamie.parentplus.control.TAG

class DownloadTask(val broadcastManager:LocalBroadcastManager) :
    AsyncTask<Unit, Unit, String>() {

    //down load file which stores the location and picture uploaded by FunPlus user
    override fun doInBackground(vararg params: Unit): String {

        val url = URL("")
        val urlConnection = url.openConnection() as HttpURLConnection
        urlConnection.connect()
        val inputStream: InputStream = urlConnection.getInputStream()
        val allText = inputStream.bufferedReader().use {
            it.readText()
        }
        val result = StringBuilder()
        result.append(allText)
        val resultString = result.toString()
        inputStream.close()

        return resultString
    }

    //when task is done, broadcast result
    override fun onPostExecute(result: String?) {
        broadcastGeotaggedPicture(result!!)
    }


    fun broadcastGeotaggedPicture(  data:String) {
        val intent = Intent(INTENT_BROADCAST_GEOTAGGEDPICTURE_CHANGED)
        intent.putExtra(INTENT_EXTRA_GEOTAGGEDPICTURE_DATA, data)
        broadcastManager.sendBroadcast(intent)
    }


}
