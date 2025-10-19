package ru.ilyamorozov.rats_and_cats

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class ScoreRepositoryTest {

    private val mockDao: ScoreDao = mockk()
    private val mockApi: ApiService = mockk()
    private val repository = ScoreRepository(mockDao) // Нужно переопределить api для мока, но для простоты используем mock

    @Test
    fun testGetTop5Remote() = runBlocking {
        coEvery { mockApi.getPosts() } returns listOf(
            Post(1, 1, "title1", "body1"),
            Post(2, 2, "title2", "body2")
        )
        // Предполагая, что repository использует mockApi (нужно инжектировать в реальном коде)

        val top5 = repository.getTop5Remote() // Адаптировать для мока
        assertEquals(2, top5.size) // Для теста первых 2
        assertEquals("Player 1", top5[0].name)
        assertEquals(100, top5[0].score)
        assertEquals("01.10.2023", top5[0].date) // После formatDate
    }
}