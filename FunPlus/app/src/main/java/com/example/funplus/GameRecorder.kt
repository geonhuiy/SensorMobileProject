package com.example.funplus

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.room.*

@Entity
data class GameRecorder(@PrimaryKey(autoGenerate = true) val id: Int, val num1:Int, val num2: Int, val sign: String){
    override fun toString(): String = "id: $id, num1: $num1, sign: $sign, num2: $num2"
}


@Dao
interface GameRecorderDao {
    @Query("SELECT * FROM gamerecorder")
    fun getAll(): LiveData<List<GameRecorder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(game: GameRecorder): Long

    @Update
    fun update(game: GameRecorder)

    //get last game by id
    @Query("SELECT * FROM gamerecorder")
    // the @Relation do the INNER JOIN for you ;)
    fun getLastGame(): GameRecorder
}



class GameModel(application: Application):
    AndroidViewModel(application) {

    private val games: LiveData<List<GameRecorder>> =
        GameDB.get(getApplication()).gameDao().getAll()

    fun getGames() = games
}