package com.example.funplus.model

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


