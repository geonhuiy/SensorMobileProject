package com.example.funplus

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.room.*
import java.util.*

@Entity
data class GameData(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val game: Game,
    val winCount: Int,
    val loseCount: Int,
    val time: Date
) {
    override fun toString(): String =
        "id: $id, game: $game, winCount: $winCount, loseCount: $loseCount, time: $time"
}


@Dao
interface GameDao {
    @Query("SELECT * FROM gamedata")
    fun getAll(): List<GameData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(game: GameData): Long

    @Update
    fun update(game: GameData)

    @Query("SELECT * FROM gamedata WHERE gamedata.id= :gameId")
    fun getGameById(gameId: Int): GameData

    @Query("SELECT gamedata.id FROM GameData WHERE gamedata.game= :game ")
    fun getGameId(game: Game): Int

    @Query("SELECT gamedata.winCount FROM GameData WHERE gamedata.id= :gameId")
    fun getWinCount(gameId: Int): Int

    @Query("SELECT gamedata.loseCount FROM GameData WHERE gamedata.id= :gameId")
    fun getLoseCount(gameId: Int): Int

    @Query("UPDATE gamedata SET winCount = winCount+1 WHERE gamedata.id= :gameId")
    fun updateWinCount(gameId: Int)

    @Query("UPDATE gamedata SET loseCount = loseCount+1 WHERE gamedata.id= :gameId")
    fun updateLoseCount(gameId: Int)

    @Query("SELECT * FROM GameData ORDER BY gamedata.time DESC LIMIT 1")
    fun getLastGame(): GameData

}


class GameModel(application: Application) : AndroidViewModel(application) {
    fun getAllGames() = GameDB.get(getApplication()).gameDao().getAll()
    fun getGameById(gameId: Int) = GameDB.get(getApplication()).gameDao().getGameById(gameId)
    fun updateWinCount(gameId: Int) = GameDB.get(getApplication()).gameDao().updateWinCount(gameId)
    fun updateLoseCount(gameId: Int) = GameDB.get(getApplication()).gameDao().updateLoseCount(gameId)

    fun getLastGame() = GameDB.get(getApplication()).gameDao().getLastGame()

}