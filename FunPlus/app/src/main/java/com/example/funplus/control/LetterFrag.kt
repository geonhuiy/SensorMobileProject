package com.example.funplus.control

import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.funplus.R
import com.example.funplus.utility.AudioPlayer
import com.example.funplus.utility.AudioRecorder
import kotlinx.android.synthetic.main.fragment_letter.*
import org.jetbrains.anko.image
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class LetterFrag : Fragment() {
    val wordToImg = mutableMapOf<String, Int>(
        Pair("apple", R.drawable.apple),
        Pair("banana", R.drawable.banana),
        Pair("cake", R.drawable.cake),
        Pair("dog", R.drawable.dog),
        Pair("egg", R.drawable.egg)
    )
    val wordList = mutableListOf<String>("Apple", "Banana", "Cake", "Dog", "Egg", "Fan")
    val imgList = mutableListOf<Int>(
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
        val view = inflater?.inflate(R.layout.fragment_letter, container, false)
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        audioRecorder = AudioRecorder(this.context!!)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        recordBtn.setOnTouchListener(object : View.OnTouchListener {

            //record when button down, play when button up
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        recordAudio()
                    }
                    MotionEvent.ACTION_UP -> {
                        playAudio()
                    }
                }
                return v?.onTouchEvent(event) ?: true
            }
        })

        var currentIndex = 0
        goNextBtn.setOnClickListener {
            if (currentIndex < imgList.size-1) {
                currentIndex++
                wordImgBtn.setImageResource(imgList[currentIndex])
                wordTv.text = wordList[currentIndex]
            } else  {
                currentIndex = 0
                wordImgBtn.setImageResource(imgList[currentIndex])
                wordTv.text = wordList[currentIndex]
            }
        }

        goBackBtn.setOnClickListener {
            if (currentIndex > 0) {
                wordImgBtn.setImageResource(imgList[currentIndex -1])
                wordTv.text = wordList[currentIndex -1]
                currentIndex--
            }else {
                currentIndex = imgList.size-1
                wordImgBtn.setImageResource(imgList[currentIndex])
                wordTv.text = wordList[currentIndex]
            }
        }
    }


    //create thread and start recording
    private fun recordAudio() {
        Log.d(TAG, "record audio")
        val recordThread = Thread(audioRecorder)
        audioRecorder.isRecording = true
        recordThread.start()
    }


    //get recorded file(via same path as stored when recording) to create inputstream
    // create thread, and play audio
    private fun playAudio() {
        Log.d(TAG, "play audio")
        audioRecorder.isRecording = false
        val fileName = "mRec.raw"
        val storageDir = this.context!!.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        lateinit var file: File
        try {
            file = File(storageDir.toString() + "/" + fileName)
        } catch (ex: IOException) {
            Log.d(TAG, ex.message!!)
        }
        val inputStream = FileInputStream(file)
        val playAudio = AudioPlayer(inputStream)
        val playAudioThread = Thread(playAudio)
        playAudioThread.start()
    }
}

