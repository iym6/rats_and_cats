package ru.ilyamorozov.rats_and_cats.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "score_record")
data class ScoreRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val playerName: String,
    val score: Int,
    val date: String
)