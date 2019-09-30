package com.example.funplus.utility

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import java.io.IOException
import java.io.InputStream

class AudioPlayer(val inputStream: InputStream) : Runnable {
    override fun run() {

        val minBufferSize = AudioTrack.getMinBufferSize(
            44100, AudioFormat.CHANNEL_OUT_STEREO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        val builder = AudioTrack.Builder()
        val attributes: AudioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()

        val audioFormat: AudioFormat = AudioFormat.Builder()
            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            .setSampleRate(44100)
            .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
            .build()

        val audioTrack = builder.setAudioAttributes(attributes)
            .setAudioFormat(audioFormat)
            .setBufferSizeInBytes(minBufferSize)
            .build()

        audioTrack.setVolume(0.8f)
        audioTrack.play()


        var i = 0
        val buffer = ByteArray(minBufferSize)
        try {
            i = inputStream.read(buffer, 0, minBufferSize)
            while (i != -1) {
                audioTrack.write(buffer, 0, i)
                i = inputStream.read(buffer, 0, minBufferSize)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }


        try {
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        audioTrack.stop()
        audioTrack.release()

    }

}