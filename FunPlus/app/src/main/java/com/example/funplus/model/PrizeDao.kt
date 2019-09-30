package com.example.funplus.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.room.*

@Dao
interface PrizeDao {
    @Query("SELECT * FROM prize")
    fun getAllPrizes(): List<Prize>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(prize: Prize): Long

    @Update
    fun update(prize: Prize)

    @Query("SELECT * FROM prize WHERE prize.id= :prizeId")
    fun getPrizeById(prizeId: Int): Prize

    @Query("SELECT prize.count FROM prize WHERE prize.imgToScan= :imgToScan")
    fun getPrizeCount(imgToScan: String): Int

    @Query("UPDATE prize SET count = count+1 WHERE prize.imgToScan= :imgToScan")
    fun updatePrizeCount(imgToScan: String)
}

class PrizeModel(application: Application) : AndroidViewModel(application) {
    fun getAllPrizes() = PrizeDB.get(getApplication()).prizeDao().getAllPrizes()
    fun getPrizeById(prizeId: Int) = PrizeDB.get(getApplication()).prizeDao().getPrizeById(prizeId)
    fun updatePrizeCount(imgName: String) = PrizeDB.get(getApplication()).prizeDao().updatePrizeCount(imgName)
    fun getPrizeCount(imgName: String) = PrizeDB.get(getApplication()).prizeDao().getPrizeCount(imgName)
}


