package ru.ilyamorozov.rats_and_cats

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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
            posts.map { post ->
                val rawDate = "2023-10-${post.id.toString().padStart(2, '0')}"
                RemoteRecord(
                    name = "Player ${post.userId}",
                    score = post.id * 100,
                    date = rawDate.formatDate()
                )
            }
        }
    }
}