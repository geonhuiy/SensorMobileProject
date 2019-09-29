package com.example.funplus.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [(GameData::class)],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class GameDB : RoomDatabase() {
    abstract fun gameDao(): GameDao
    companion object {
        private var gameDB: GameDB? = null
        @Synchronized
        fun get(context: Context): GameDB {
            if (gameDB == null) {
                gameDB =
                    Room.databaseBuilder(
                        context.applicationContext,
                        GameDB::class.java, "game.gameDB"
                    ).build()
            }
            return gameDB!!
        }
    }
}