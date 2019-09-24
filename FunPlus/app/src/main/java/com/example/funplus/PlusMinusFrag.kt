package com.example.funplus

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.plus_minus_frag.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.support.v4.runOnUiThread

class PlusMinusFrag() : Fragment() {
    lateinit var correctAnswerFrag: CorrectAnswerFrag
    lateinit var wrongAnswerFrag: WrongAnswerFrag
    lateinit var fTransaction: FragmentTransaction
    lateinit var fManager: FragmentManager
    lateinit var db: GameDB
    var toRepeatGame = false
    var gameStarted = false


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
        val view = inflater?.inflate(R.layout.plus_minus_frag, container, false)
        return view
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        db = GameDB.get(this.context!!)
        fManager = this!!.fragmentManager!!
        correctAnswerFrag = CorrectAnswerFrag()
        wrongAnswerFrag = WrongAnswerFrag()


        if (!gameStarted) {
            resetGame()
        } else {
            if (toRepeatGame) {
                Toast.makeText(this.context, "to repeat game", Toast.LENGTH_LONG).show()
                repeatGame()
            }else {
                resetGame()
            }
        }

        okBtn.setOnClickListener {
            val correctAnswer = getAnswer()
            val answerTxt = answerEt.text.toString()
            if (answerTxt.isEmpty() || answerTxt.equals("")){
                Toast.makeText(this.context, "Input a number", Toast.LENGTH_LONG).show()
            }else{
                try {
                    val answer = answerTxt.toInt()
                    if (answer == correctAnswer) goToAnswerFrag(correctAnswerFrag) else goToAnswerFrag(wrongAnswerFrag)
                }catch (e:Exception){
                    Toast.makeText(this.context, "Answer has to be a number", Toast.LENGTH_LONG).show()
                    Log.d(TAG, e.message!!)
                }
            }
        }

    }

    private fun resetGame() {
        Log.d(TAG, "set new numbers")
        var num1 = (1..10).random()
        var num2 = (1..10).random()
        val sign = mutableListOf<String>("+", "-").random()
        if (sign.equals("-") && num1 < num2) {
            val tmp = num1
            num1 = num2
            num2 = tmp
        }

        num1Btn.text = num1.toString()
        num2Btn.text = num2.toString()
        signBtn.text = sign

        doAsync{
           // db.clearAllTables()
            val id = db.gameDao().update(GameRecorder(25, num1, num2, sign))

        }

        runOnUiThread {
             //Log.d(TAG, "id??? $id")
        }
    }

    private fun repeatGame() {
        val gameModel =
            ViewModelProviders.of(this).get(GameModel::class.java)
        gameModel.getGames().observe(this, Observer {
            val lastGame = it.last()
            num1Btn.text = lastGame!!.num1.toString()
            num2Btn.text = lastGame!!.num2.toString()
            signBtn.text = lastGame!!.sign
            Log.d(TAG+"lastGame: ", lastGame.toString())
        })
    }


    private fun goToAnswerFrag(frag: Fragment) {
        Log.d(TAG, "goToAnswerFrag")
        fTransaction = fManager.beginTransaction()
        fTransaction.replace(R.id.fcontainer, frag)
        //fTransaction.addToBackStack(null)
        fTransaction.commit()
    }

    private fun getAnswer(): Int {
        Log.d(TAG, "getAnswer")
        val num1 = num1Btn.text.toString().toInt()
        val num2 = num2Btn.text.toString().toInt()
        val sign = signBtn.text.toString()
        return if (sign.equals("+")) num1 + num2 else num1 - num2
    }
}