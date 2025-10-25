package ru.ilyamorozov.rats_and_cats

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import ru.ilyamorozov.rats_and_cats.data.local.AppDatabase
import ru.ilyamorozov.rats_and_cats.data.local.ScoreDao
import ru.ilyamorozov.rats_and_cats.data.local.ScoreRecord
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class ScoreDaoTest {

    private lateinit var dao: ScoreDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        dao = db.scoreDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun testInsertAndGetAll() = runBlocking {
        val score1 = ScoreRecord(playerName = "Player1", score = 100, date = "01.01.2023")
        val score2 = ScoreRecord(playerName = "Player2", score = 200, date = "02.01.2023")
        dao.insert(score1)
        dao.insert(score2)

        val scores = dao.getAllScores().first()
        assertEquals(2, scores.size)
        assertEquals(200, scores[0].score) // Отсортировано по убыванию
        assertEquals(100, scores[1].score)
    }
}