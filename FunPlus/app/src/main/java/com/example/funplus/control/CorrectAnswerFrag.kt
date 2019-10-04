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
import com.example.funplus.utility.SoundEffectPlayer
import kotlinx.android.synthetic.main.correct_answer_frag.*
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class CorrectAnswerFrag : Fragment() {
    companion object {
        val numList = listOf<Int>(123, 234, 345, 456, 567, 678)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        //user answer is correct, play a cheering sound
        playCheeringSound()
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

        //click camera button to start AR activity(open camera and start scanning)
        goToScanBtn.setOnClickListener {
            val intent = Intent(this.context, ArActivity::class.java)
            intent.putExtra("randomNum", num)
            startActivity(intent)
            Toast.makeText(this.context, "go to ArActivity to scan image", Toast.LENGTH_LONG).show()
            this.requireActivity().supportFragmentManager.popBackStack()
        }
    }

    //show a random number from a predefined list
    private fun showNumber(): Int {
        val randomNum = numList.random()
        numTv.text = randomNum.toString()
        Log.d(TAG + "showNumber - num: ", numTv.text.toString())
        return randomNum
    }

    //play a random cheering sound
    private fun playCheeringSound() {
        val soundList = listOf<Int>(R.raw.unbelievable, R.raw.woohoo, R.raw.yay)
        SoundEffectPlayer.playSound(this.requireActivity(), soundList.random())
    }

}