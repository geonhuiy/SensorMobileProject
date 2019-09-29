package com.example.funplus.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel


class GameModel(application: Application) : AndroidViewModel(application) {
    fun getAllGames() = GameDB.get(getApplication()).gameDao().getAll()
    fun getGameById(gameId: Int) = GameDB.get(getApplication()).gameDao().getGameById(gameId)
    fun updateWinCount(gameId: Int) = GameDB.get(getApplication()).gameDao().updateWinCount(gameId)
    fun updateLoseCount(gameId: Int) = GameDB.get(getApplication()).gameDao().updateLoseCount(gameId)
    fun getLastGame() = GameDB.get(getApplication()).gameDao().getLastGame()

}