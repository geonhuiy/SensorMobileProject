package com.example.funplus.control

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.funplus.R
import com.example.funplus.utility.CAMERA
import com.example.funplus.utility.CAMERA_AR_REQUEST_CODE
import com.example.funplus.utility.PermissionChecker
import com.example.funplus.utility.SoundEffectPlayer
import kotlinx.android.synthetic.main.correct_answer_frag.*

class CorrectAnswerFrag : Fragment() {
    lateinit var fTransaction: FragmentTransaction
    lateinit var fManager: FragmentManager
    lateinit var arFrag: ArFragMain
    val numList = listOf<Int>(123, 234, 345, 456, 567, 678)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        //user answer is correct, play a cheering sound
        fManager = this.fragmentManager!!
        arFrag = ArFragMain()
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
        showNumber()

        //click camera button to start AR activity(open camera and start scanning)
        goToScanBtn.setOnClickListener {
            goToArFrag()
            Toast.makeText(this.context, "go to ArFragMain to scan image", Toast.LENGTH_LONG).show()
            this.requireActivity().supportFragmentManager.popBackStack()
        }
    }

    //show a random number from a predefined list
    private fun showNumber() {
        val randomNum = numList.random()
        numTv.text = randomNum.toString()
        Log.d(TAG + "showNumber - num: ", numTv.text.toString())
        val bundle = Bundle()
        bundle.putInt("randomNum", randomNum)
        arFrag.arguments = bundle
    }

    //play a random cheering sound
    private fun playCheeringSound() {
        val soundList = listOf<Int>(R.raw.unbelievable, R.raw.woohoo, R.raw.wow)
        SoundEffectPlayer.playSound(this.requireActivity(), soundList.random())
    }

    private fun goToArFrag() {
        Log.d(TAG, "goToArFrag")
        PermissionChecker.askForPermissionIfNotGranted(this.context!!, this.requireActivity(), CAMERA_AR_REQUEST_CODE, CAMERA)
        fTransaction = fManager.beginTransaction()
        fTransaction.replace(R.id.fcontainer, arFrag)
        fTransaction.commit()
    }

}