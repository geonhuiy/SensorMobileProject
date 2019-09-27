package com.example.funplus

import androidx.room.TypeConverter
import java.util.*

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date?{
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long?{
        return date?.time?.toLong()
    }

    @TypeConverter
    fun gameToString(game: Game?): String?{
        return game.toString()
    }

    @TypeConverter
    fun stringToGame(value: String?): Game?{
        val slicedValue = value?.split(" ")
        val num1 = slicedValue!![0].toInt()
        val sign = slicedValue[1]
        val num2 = slicedValue[2].toInt()

        return Game(num1,num2,sign)
    }
}