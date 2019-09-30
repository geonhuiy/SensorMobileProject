package com.example.funplus.model

import android.graphics.drawable.Drawable
import androidx.room.*

@Entity
data class Prize(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val imgToScan: String,
    val prizeImg: Int,
    val count: Int
)




