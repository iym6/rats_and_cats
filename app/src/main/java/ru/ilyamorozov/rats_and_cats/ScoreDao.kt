package ru.ilyamorozov.rats_and_cats

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScoreDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(score: ScoreRecord)

    @Query("SELECT * FROM score_record ORDER BY score DESC")
    fun getAllScores(): Flow<List<ScoreRecord>>
}