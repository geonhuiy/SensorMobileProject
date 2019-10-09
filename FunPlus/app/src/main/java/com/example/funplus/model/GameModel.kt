package com.example.funplus.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import java.util.*


class GameModel(application: Application) : AndroidViewModel(application) {
    fun getAllGames() = GameDB.get(getApplication()).gameDao().getAll()
    fun getGameById(gameId: Int) = GameDB.get(getApplication()).gameDao().getGameById(gameId)
    fun updateWinCount(gameId: Int, time: Date) = GameDB.get(getApplication()).gameDao().updateWinCount(gameId, time)
    fun updateLoseCount(gameId: Int, time: Date) = GameDB.get(getApplication()).gameDao().updateLoseCount(gameId, time)
    fun getLastGame() = GameDB.get(getApplication()).gameDao().getLastGame()

}