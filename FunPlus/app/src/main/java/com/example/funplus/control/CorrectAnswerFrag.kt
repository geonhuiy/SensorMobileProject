package com.example.funplus.control

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.funplus.R
import com.example.funplus.utility.AudioPlayer
import kotlinx.android.synthetic.main.correct_answer_frag.*
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class CorrectAnswerFrag : Fragment() {
    val numList = listOf<Int>(123, 234, 345, 456, 567, 678)
    lateinit var arActivity: ArActivity
    companion object {

        @JvmStatic
        fun newInstance() =
            CorrectAnswerFrag().apply {
                arguments = Bundle().apply {
                    // putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            // columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater?.inflate(R.layout.correct_answer_frag, container, false)
        return view
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //show a random number from a predefined list
        val num = showNumber()

        //play audio of the shown number when audio button is clicked
        playNumAudioBtn.setOnClickListener{
            Log.d(TAG, "play audio button clicked")
            val file = getAudioFile(num)
            playAudio(file)
        }


        goToScanBtn.setOnClickListener {
            val intent = Intent(this.context, ArActivity::class.java)
            intent.putExtra("randomNum", num)
            startActivity(intent)
            Toast.makeText(this.context, "go to ArActivity to scan image", Toast.LENGTH_LONG).show()
            activity!!.supportFragmentManager.popBackStack()
        }
    }

    private fun showNumber(): Int{
       // val numList = listOf<Int>(123, 234, 456, 567, 789,890)
        val randomNum = numList.random()
        numTv.text = randomNum.toString()
        Log.d(TAG+"showNumber - num: ", numTv.text.toString())
        return randomNum
    }


    /*fetch audio file from file storage
    * audio files are named same as the numbers shown on UI
    */
    private fun getAudioFile(num: Int): File {
        Log.d(TAG+"getAudioFile - num: ", num.toString())
        val audioFileName = "$num.wav"
        Log.d(TAG+"audioFileName: ", audioFileName)

        //to be decided later about where to store pre-made audio files
        //val storageDir = this.context!!.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
        lateinit var file: File
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
        ) {
            try {
            file = File( storageDir, audioFileName)
            Log.d(TAG+"file absolutePath: ", file.absolutePath)
            } catch (ex: IOException) {
                Log.d(TAG, ex.message!!)
            }
        }
        return file
    }

    //create thread, pass the fetched audio file to create inputstream and start thread to play audio
    private fun playAudio(file: File) {
    val inputStream = FileInputStream(file)

  //  private fun playAudio() {
   //   val inputStream = resources.openRawResource(R.raw.wav)
        val playAudio = AudioPlayer(inputStream)
        val playAudioThread = Thread(playAudio)
        playAudioThread.start()
    }

}