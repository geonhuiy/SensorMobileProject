package com.example.funplus.control

import android.Manifest
import android.content.res.Configuration
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.funplus.R
import com.example.funplus.utility.*
import kotlinx.android.synthetic.main.word_frag.*
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.lang.Exception

class LetterFrag() : Fragment() {
    //a list of words to show on the UI
    private val wordList = mutableListOf<String>("Apple", "Banana", "Cake", "Dog", "Egg", "Fan")
    private lateinit var fTransaction: FragmentTransaction
    private lateinit var fManager: FragmentManager

    //a list of images matching with the words to show on the UI
    private val imgList = mutableListOf<Int>(
        R.drawable.apple,
        R.drawable.banana,
        R.drawable.cake,
        R.drawable.dog,
        R.drawable.egg,
        R.drawable.fan
    )

    lateinit var audioRecorder: AudioRecorder

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater?.inflate(R.layout.word_frag, container, false)
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //check for audio permission, ask for it during runtime if not granted
        PermissionChecker.askForPermissionIfNotGranted(
            this.context!!,
            this.requireActivity(),
            RECORD_AUDIO_REQUEST_CODE,
            RECORD_AUDIO
        )
        audioRecorder = AudioRecorder(this.context!!)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT || newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            try {
                fManager = this.fragmentManager!!
                fTransaction = fManager.beginTransaction()
                fTransaction.detach(this).attach(this).commit()
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        recordBtn.setOnTouchListener { v, event ->
            //record when button down, play when button up
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    recordAudio()
                }
                MotionEvent.ACTION_UP -> {
                    playAudio()
                }
            }
            v?.onTouchEvent(event) ?: true
        }

        //show next image+word, loop through the list, start from first when reach the last
        var currentIndex = 0
        goNextBtn.setOnClickListener {
            if (currentIndex < imgList.size - 1) {
                currentIndex++
                wordIv.setImageResource(imgList[currentIndex])
                wordTv.text = wordList[currentIndex]
            } else {
                currentIndex = 0
                wordIv.setImageResource(imgList[currentIndex])
                wordTv.text = wordList[currentIndex]
            }
        }

        //show previous image+word, start from the last when reach the first
        goBackBtn.setOnClickListener {
            if (currentIndex > 0) {
                wordIv.setImageResource(imgList[currentIndex - 1])
                wordTv.text = wordList[currentIndex - 1]
                currentIndex--
            } else {
                currentIndex = imgList.size - 1
                wordIv.setImageResource(imgList[currentIndex])
                wordTv.text = wordList[currentIndex]
            }
        }
    }


    //create thread and start recording
    private fun recordAudio() {
        val recordThread = Thread(audioRecorder)
        audioRecorder.isRecording = true
        recordThread.start()
    }


    //get recorded file(via same path as stored when recording) to create inputstream
    // create thread, and play audio
    private fun playAudio() {
        audioRecorder.isRecording = false
        val fileName = "mRec.raw"

        val storageDir = this.context!!.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        lateinit var file: File
        try {
            file = File(storageDir.toString() + "/" + fileName)
        } catch (ex: IOException) {
        }
        val inputStream = FileInputStream(file)
        val playAudio = AudioPlayer(inputStream)
        val playAudioThread = Thread(playAudio)
        playAudioThread.start()
    }
}

