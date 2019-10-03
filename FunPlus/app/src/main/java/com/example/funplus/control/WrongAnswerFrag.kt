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
import com.example.funplus.utility.SoundEffectPlayer
import kotlinx.android.synthetic.main.wrong_answer_frag.*

class WrongAnswerFrag : Fragment(){
    lateinit var fTransaction: FragmentTransaction
    lateinit var fManager: FragmentManager
    lateinit var plusMinusFrag: PlusMinusFrag


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SoundEffectPlayer.playSound(this.requireActivity(), R.raw.ohthatsokay)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater?.inflate(R.layout.wrong_answer_frag, container, false)
        return view
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        fManager = this!!.fragmentManager!!
        plusMinusFrag = PlusMinusFrag()

        plusMinusFrag.gameStarted = true

        tryAgainBtn.setOnClickListener {
            plusMinusFrag.toRepeatGame = true
            showPlusMinusFrag(plusMinusFrag)
            Toast.makeText(this.context, "try again", Toast.LENGTH_LONG).show()
        }

        newGameBtn.setOnClickListener {
            showPlusMinusFrag(plusMinusFrag)
            Toast.makeText(this.context, "new game", Toast.LENGTH_LONG).show()
        }

    }


    private fun showPlusMinusFrag(frag : Fragment) {
        Log.d(TAG, "showPlusMinusFrag from wrong answer frag")
        fTransaction = fManager.beginTransaction()
        fTransaction.replace(R.id.fcontainer, frag)
        //fTransaction.addToBackStack(null)
        fTransaction.commit()
    }


}