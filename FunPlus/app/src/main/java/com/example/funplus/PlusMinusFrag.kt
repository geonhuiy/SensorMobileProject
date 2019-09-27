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
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.plus_minus_frag.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*

class PlusMinusFrag() : Fragment() {
    lateinit var correctAnswerFrag: CorrectAnswerFrag
    lateinit var wrongAnswerFrag: WrongAnswerFrag
    lateinit var fTransaction: FragmentTransaction
    lateinit var fManager: FragmentManager
    lateinit var gameDB: GameDB
    private var gameId = 0
    var toRepeatGame = false
    var gameStarted = false
    lateinit var newGame: Game


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
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

        gameDB = GameDB.get(this.context!!)
        fManager = this!!.fragmentManager!!
        correctAnswerFrag = CorrectAnswerFrag()
        wrongAnswerFrag = WrongAnswerFrag()

        /*set game on the UI */
        setGame()

        okBtn.setOnClickListener {
            val correctAnswer = getAnswer()
            val answerTxt = answerEt.text.toString()
            if (answerTxt.isEmpty() || answerTxt.equals("")) {
                Toast.makeText(this.context, "Input a number", Toast.LENGTH_LONG).show()
            } else {
                try {
                    val answer = answerTxt.toInt()
                    if (answer == correctAnswer) {
                        newGame?.let { it -> insertOrUpdateGameDB(it, true) }
                        goToAnswerFrag(correctAnswerFrag)
                    } else {
                        newGame?.let { it1 -> insertOrUpdateGameDB(it1, false) }
                        goToAnswerFrag(wrongAnswerFrag)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.d(TAG, e.toString())
                }
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

    /*create a new game with two randowm numbers from 1 to 10
     * and random sign of "+" or "-"
     */
    private fun createNewGame() {
        Log.d(TAG, "set new numbers")
        var num1 = (1..10).random()
        var num2 = (1..10).random()
        val sign = mutableListOf<String>("+", "-").random()
        if (sign.equals("-") && num1 < num2) {
            val tmp = num1
            num1 = num2
            num2 = tmp
        }
        setGameOnUI(Game(num1, num2, sign))
    }

    /*check if the game is stored in db already
     *if yes, update the winCount/loseCount
     * if not, insert a new game into db
     */
    private fun insertOrUpdateGameDB(game: Game, isAnswerCorrect: Boolean) {
        Log.d(TAG, "insertOrUpdateGameDB, game: " + game.toString())
        doAsync {
            if (isGameInDB(game)) {
                val gameId = gameDB.gameDao().getGameId(game)
                Log.d(TAG, "gameID: " + gameId.toString())

                //update existing game info
                if (isAnswerCorrect) {
                    Log.d(TAG + "getWinCount", gameDB.gameDao().getWinCount(gameId).toString())
                    gameDB.gameDao().updateWinCount(gameId)
                    Log.d(TAG + "getWinCount", gameDB.gameDao().getWinCount(gameId).toString())
                } else {
                    Log.d(TAG + "getLoseCount", gameDB.gameDao().getLoseCount(gameId).toString())
                    gameDB.gameDao().updateLoseCount(gameId)
                    Log.d(TAG + "getLoseCount", gameDB.gameDao().getLoseCount(gameId).toString())
                }

            } else {
                //create a new game
                Log.d(TAG, "game not in DB")
                if (isAnswerCorrect) {
                    gameId = gameDB.gameDao().insert(GameData(0, game, 1, 0, Date())).toInt()
                } else {
                    gameId = gameDB.gameDao().insert(GameData(0, game, 0, 1, Date())).toInt()
                }
            }
        }
    }

    /*check if a game is stored in DB
     */
    private fun isGameInDB(game: Game): Boolean {
        val allGames = gameDB.gameDao().getAll()
        Log.d(TAG, "isGameInDB():" + allGames.size + " games stored in DB")
        //allGames = ViewModelProviders.of(this).get(GameModel::class.java).getAllGames()
        var foundGameInDB = false
        for (gameData: GameData in allGames!!) {
            if (gameData.game.equals(game)) {
                foundGameInDB = true
                break
            }
        }
        Log.d(TAG, "isGameInDB:" + foundGameInDB)
        return foundGameInDB
    }

    /*if user wants to repeat a game
     * fetch the previous/last game from db
     * and set it on UI
     */
    private fun repeatGame() {
        Log.d(TAG, "repeatGame")
        var previousGame: Game? = null
        val gameModel = ViewModelProviders.of(this).get(GameModel::class.java)
        doAsync {
            Log.d(TAG, gameModel.getLastGame().toString())
            previousGame = gameModel.getLastGame().game
            Log.d(TAG, previousGame.toString())

            uiThread {
                setGameOnUI(previousGame!!)
            }
        }
    }

    //set the given game on UI
    private fun setGameOnUI(game: Game) {
        newGame = game
        Log.d(TAG, "setGameOnUI: " + game.num1 + game.sign + game.num2)
        num1Btn.text = game.num1.toString()
        num2Btn.text = game.num2.toString()
        signBtn.text = game.sign
    }

    /*navigate to answer fragment based on user's answer
     *if answer is correct, go to the correctAnswer frag
     * else, go to the wrongAnswer frag
     */
    private fun goToAnswerFrag(frag: Fragment) {
        Log.d(TAG, "goToAnswerFrag")
        fTransaction = fManager.beginTransaction()
        fTransaction.replace(R.id.fcontainer, frag)
        //fTransaction.addToBackStack(null)
        fTransaction.commit()
    }

    /*calculate the correct answer of the game shown on UI */
    private fun getAnswer(): Int {
        val num1 = num1Btn.text.toString().toInt()
        val num2 = num2Btn.text.toString().toInt()
        val sign = signBtn.text.toString()
        var answer = 0
        if (sign.equals("+")) {
            answer = num1 + num2
        } else {
            answer = num1 - num2
        }
        Log.d(TAG, "getAnswer: " + num1 + sign + num2 + "=" + answer)
        return answer
    }
}