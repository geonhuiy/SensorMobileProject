package com.example.funplus.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [(Prize::class)],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class PrizeDB : RoomDatabase() {
    abstract fun prizeDao(): PrizeDao
    companion object {
        private var prizeDB: PrizeDB? = null
        @Synchronized
        fun get(context: Context): PrizeDB {
            if (prizeDB == null) {
                prizeDB =
                    Room.databaseBuilder(
                        context.applicationContext,
                        PrizeDB::class.java, "prize.prizeDB"
                    ).build()
            }
            return prizeDB!!
        }
    }
}