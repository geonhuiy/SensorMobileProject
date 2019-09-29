package com.example.funplus.model

import androidx.room.*

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
