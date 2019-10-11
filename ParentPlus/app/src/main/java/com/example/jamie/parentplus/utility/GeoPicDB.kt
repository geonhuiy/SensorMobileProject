package com.example.jamie.parentplus.utility

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.*


@Database(
    entities = [(GeoPicData::class)],
    version = 1
)

@TypeConverters(Converters::class)
abstract class GeoPicDB : RoomDatabase() {
    abstract fun geoPicDao(): GeoPicDao
    companion object {
        private var geoPicDB: GeoPicDB? = null
        @Synchronized
        fun get(context: Context): GeoPicDB {
            if (geoPicDB == null) {
                geoPicDB =
                    Room.databaseBuilder(
                        context.applicationContext,
                        GeoPicDB::class.java, "game.geoPicDB"
                    ).build()
            }
            return geoPicDB!!
        }
    }
}


@Entity
data class GeoPicData(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val data: String,
    val time: Date
) {
    override fun toString(): String =
        "id: $id, data: $data time: $time"
}


@Dao
interface GeoPicDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(geoPicData: GeoPicData): Long

    @Query("SELECT * FROM GeoPicData ORDER BY  time")
    fun getAll(): LiveData<List<GeoPicData>>

    @Query("SELECT * FROM GeoPicData ORDER BY  time DESC  LIMIT 1")
    fun getLastGeoPic(): GeoPicData
}


class GeoPicModel(application: Application) : AndroidViewModel(application) {
    fun getLastGeoPic() = GeoPicDB.get(getApplication()).geoPicDao().getLastGeoPic()
    fun getAllGeoPic() = GeoPicDB.get(getApplication()).geoPicDao().getAll()
}


class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date?{
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long?{
        return date?.time?.toLong()
    }
}