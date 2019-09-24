package com.example.funplus

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [(GameRecorder::class)],
    version = 1
)
abstract class GameDB : RoomDatabase() {
    abstract fun gameDao(): GameRecorderDao
    companion object {
        private var sInstance: GameDB? = null
        @Synchronized
        fun get(context: Context): GameDB {
            if (sInstance == null) {
                sInstance =
                    Room.databaseBuilder(
                        context.applicationContext,
                        GameDB::class.java, "game.db"
                    ).build()
            }
            return sInstance!!
        }
    }
}