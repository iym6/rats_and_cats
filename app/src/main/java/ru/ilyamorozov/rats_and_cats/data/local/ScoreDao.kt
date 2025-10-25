package ru.ilyamorozov.rats_and_cats.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.ilyamorozov.rats_and_cats.data.local.ScoreRecord

@Dao
interface ScoreDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(score: ScoreRecord)

    @Query("SELECT * FROM score_record ORDER BY score DESC")
    fun getAllScores(): Flow<List<ScoreRecord>>

    @Query("SELECT * FROM score_record WHERE playerName = :playerName LIMIT 1")
    suspend fun getScoreByPlayerName(playerName: String): ScoreRecord?

    @Update
    suspend fun update(score: ScoreRecord)
}