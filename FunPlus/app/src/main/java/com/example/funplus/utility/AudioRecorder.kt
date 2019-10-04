package com.example.funplus.utility

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Environment
import android.util.Log
import com.example.funplus.control.TAG
import java.io.*
import java.util.*

class AudioRecorder(val context: Context) : Runnable {
    var isRecording: Boolean = false
    lateinit var file: File
    lateinit var dataOutputStream: DataOutputStream

    override fun run() {

        //get/make a file with given filename and directory
        val recFileName = "mRec.raw"
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        try {
            file = File(storageDir.toString() + "/" + recFileName)
        } catch (ex: IOException) {
            Log.d(TAG, ex.message!!)
        }

        try {
            val outputStream = FileOutputStream(file)
            val bufferedOutputStream = BufferedOutputStream(outputStream)
            dataOutputStream = DataOutputStream(bufferedOutputStream)

            val minBufferSize = AudioRecord.getMinBufferSize(
                44100,
                AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val audioFormat = AudioFormat.Builder()
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setSampleRate(44100)
                .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                .build()

            val recorder = AudioRecord.Builder()
                .setAudioSource(MediaRecorder.AudioSource.MIC)
                .setAudioFormat(audioFormat)
                .setBufferSizeInBytes(minBufferSize)
                .build()

            val audioData = ByteArray(minBufferSize)

            recorder.startRecording()

            while (isRecording) {
                val bytes = recorder.read(audioData, 0, minBufferSize)
                if (bytes > 0) {
                    dataOutputStream.write(audioData)
                }
            }
            recorder.stop()
            dataOutputStream.close()

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}