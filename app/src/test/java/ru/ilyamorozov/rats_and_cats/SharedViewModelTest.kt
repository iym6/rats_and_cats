package ru.ilyamorozov.rats_and_cats

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class SharedViewModelTest {
    @Test
    fun testSelectLevel() = runBlocking {
        val viewModel = SharedViewModel()
        val level = Level(1, "Test", "Easy")
        viewModel.selectLevel(level)
        assertEquals(level, viewModel.selectedLevel.first())
    }
}