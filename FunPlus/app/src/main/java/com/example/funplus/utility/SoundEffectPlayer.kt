package com.example.funplus.utility

import android.app.Activity
import com.example.funplus.R
import java.io.InputStream

object SoundEffectPlayer {
    //play given sound effect
   fun playSound(activity: Activity, source: Int)  {
       val inputStream = activity.resources.openRawResource(source)
        val playAudio = AudioPlayer(inputStream)
        val playAudioThread = Thread(playAudio)
        playAudioThread.start()
    }
}