package com.example.funplus.control

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import com.example.funplus.R
import com.example.funplus.model.Game
import com.example.funplus.model.GameDB
import com.example.funplus.model.GameData
import com.example.funplus.model.GameModel
import com.google.ar.core.Frame
import kotlinx.android.synthetic.main.number_frag.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.lang.Exception
import java.util.*

class NumberFrag() : Fragment() {
    private lateinit var correctAnswerFrag: CorrectAnswerFrag
    private lateinit var wrongAnswerFrag: WrongAnswerFrag
    private lateinit var fTransaction: FragmentTransaction
    private lateinit var fManager: FragmentManager
    private lateinit var gameDB: GameDB
    private var correctAnswer = 0
    private var userAnswer = 0
    var toRepeatGame = false
    var gameStarted = false
    private lateinit var newGame: Game


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.number_frag, container, false)
        return view
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

        gameDB = GameDB.get(this.context!!)
        fManager = this.fragmentManager!!
        correctAnswerFrag = CorrectAnswerFrag()
        wrongAnswerFrag = WrongAnswerFrag()

        /*set game on the UI */
        setGame()

        //when button clicked, check if user answer is correct or wrong, and display a frag accordingly
        okBtn.setOnClickListener {
            Log.d(TAG + "userAnswer:", userAnswer.toString())
            Log.d(TAG + "correctAnswer:", correctAnswer.toString())
            val pagerAdapter = FragViewPagerAdapter(fManager)
            if (userAnswer == correctAnswer) {
                insertOrUpdateGameDB(newGame, true)
                goToAnswerFrag(correctAnswerFrag)
                pagerAdapter.notifyDataSetChanged()
            } else {
                insertOrUpdateGameDB(newGame, false)
                goToAnswerFrag(wrongAnswerFrag)
                pagerAdapter.notifyDataSetChanged()
            }
        }
    }

    /*set a game on the UI, if user wants to redo a game,
      then show the previous one,
      otherwise create a new one
     */
    private fun setGame() {
        if (!gameStarted) {
            createNewGame()
        } else {
            if (toRepeatGame) {
                repeatGame()
            } else {
                createNewGame()
            }
        }
    }

    /*check if the game is stored in db already
     *if yes, update the winCount/loseCount
     * if not, insert a new game into db
     */
    private fun insertOrUpdateGameDB(game: Game, isAnswerCorrect: Boolean) {
        doAsync {
            if (isGameInDB(game)) {
                val gameId = gameDB.gameDao().getGameId(game)

                //update existing game info
                if (isAnswerCorrect) {
                    gameDB.gameDao().updateWinCount(gameId, Date())
                } else {
                    gameDB.gameDao().updateLoseCount(gameId, Date())
                }

            } else {
                //create a new game
                if (isAnswerCorrect) {
                    gameDB.gameDao().insert(
                        GameData(
                            0,
                            game,
                            1,
                            0,
                            Date()
                        )
                    ).toInt()
                } else {
                    gameDB.gameDao().insert(
                        GameData(
                            0,
                            game,
                            0,
                            1,
                            Date()
                        )
                    ).toInt()
                }
            }
        }
    }

    /*check if a game is stored in DB
     */
    private fun isGameInDB(game: Game): Boolean {
        val allGames = gameDB.gameDao().getAll()
        var foundGameInDB = false
        for (gameData: GameData in allGames) {
            if (gameData.game == game) {
                foundGameInDB = true
                break
            }
        }
        return foundGameInDB
    }

    /*create a new game with two randowm numbers from 1 to 10
     * and random sign of "+" or "-"
     */
    private fun createNewGame() {
        var num1 = (1..10).random()
        var num2 = (1..10).random()
        val sign = mutableListOf<String>("+", "-").random()
        if (sign.equals("-") && num1 < num2) {
            val tmp = num1
            num1 = num2
            num2 = tmp
        }
        newGame = Game(num1, num2, sign)
        setGameOnUI(newGame)
    }

    /*if user wants to repeat a game
     * fetch the previous/last game from db(game are stored with date)
     * and set it on UI
     */
    private fun repeatGame() {
        val gameModel = ViewModelProviders.of(this).get(GameModel::class.java)
        doAsync {
            val previousGame = gameModel.getLastGame().game
            uiThread {
                newGame = previousGame
                setGameOnUI(newGame)
            }
        }
    }

    //set the given game on UI
    private fun setGameOnUI(game: Game) {
        num1Btn.text = game.num1.toString()
        num2Btn.text = game.num2.toString()
        signBtn.text = game.sign
        getCorrectAnswer()
        setAnswerOptions()
        getUserAnswer()
    }

    /*navigate to answer fragment based on user's answer
     *if answer is correct, go to the correctAnswer frag
     * else, go to the wrongAnswer frag
     */
    private fun goToAnswerFrag(frag: Fragment) {
        fTransaction = fManager.beginTransaction()
        fTransaction.replace(R.id.fcontainer, frag)
        fTransaction.commit()
    }

    /*calculate the correct answer of the game shown on UI */
    private fun getCorrectAnswer() {
        if (newGame.sign.equals("+")) {
            correctAnswer = newGame.num1 + newGame.num2
        } else {
            correctAnswer = newGame.num1 - newGame.num2
        }
    }

    //set three answer options, one is the correct answer, the other 2 are random numbers
    // and display them on radio buttons
    private fun setAnswerOptions() {
        val num1: Int
        val num2: Int
        when (correctAnswer) {
            0 -> {
                num1 = (1..10).random()
                num2 = (11..20).random()
            }
            20 -> {
                num1 = (0..10).random()
                num2 = (11..19).random()
            }
            else -> {
                num1 = (0 until correctAnswer).random()
                num2 = ((correctAnswer + 1)..20).random()
            }
        }
        val options = mutableListOf<Int>(correctAnswer, num1, num2)
        val newOptions = options.shuffled()
        radioButton1.text = newOptions[0].toString()
        radioButton2.text = newOptions[1].toString()
        radioButton3.text = newOptions[2].toString()
    }


    //get user answer from clicked button
    private fun getUserAnswer() {
        radioGroup.clearCheck()
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val rb: RadioButton = group.findViewById(checkedId)
            if (checkedId > -1) {
                Toast.makeText(this.context, rb.text, Toast.LENGTH_SHORT).show()
                userAnswer = rb.text.toString().toInt()
            }
        }
    }
}