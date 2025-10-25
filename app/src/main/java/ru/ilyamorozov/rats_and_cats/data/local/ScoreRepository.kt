package ru.ilyamorozov.rats_and_cats.data.local

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.ilyamorozov.rats_and_cats.data.remote.ApiService
import ru.ilyamorozov.rats_and_cats.data.remote.RemoteRecord
import ru.ilyamorozov.rats_and_cats.util.formatDate

class ScoreRepository(private val dao: ScoreDao) {

    private val api: ApiService = Retrofit.Builder()
        .baseUrl("https://jsonplaceholder.typicode.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)

    val allScores: Flow<List<ScoreRecord>> = dao.getAllScores()

    suspend fun insert(score: ScoreRecord) {
        withContext(Dispatchers.IO) {
            dao.insert(score)
        }
    }

    suspend fun getTop5Remote(): List<RemoteRecord> {
        return withContext(Dispatchers.IO) {
            val posts = api.getPosts().take(5)
            posts.mapIndexed { index, post ->
                val rawDate = "2023-10-${post.id.toString().padStart(2, '0')}"
                RemoteRecord(
                    name = "Player ${index + 1}",
                    score = post.id * 100,
                    date = rawDate.formatDate(),
                )
            }.sortedByDescending { it.score }
        }
    }

    suspend fun getScoreByPlayerName(playerName: String): ScoreRecord? {
        return withContext(Dispatchers.IO) {
            dao.getScoreByPlayerName(playerName)
        }
    }

    suspend fun updateScore(score: ScoreRecord) {
        withContext(Dispatchers.IO) {
            dao.update(score)
        }
    }
}