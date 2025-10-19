package ru.ilyamorozov.rats_and_cats

import org.junit.Test
import org.junit.Assert.*

class MouseTest {

    @Test
    fun testGetMouseInfo_WithNameAndSpeed() {
        // Arrange
        val mouse = Mouse("Джерри", 15)

        // Act
        val result = mouse.getMouseInfo()

        // Assert
        assertEquals("Мышь по имени Джерри бежит со скоростью 15", result)
    }

    @Test
    fun testGetMouseInfo_WithEmptyName() {
        // Arrange
        val mouse = Mouse(speed = 10)

        // Act
        val result = mouse.getMouseInfo()

        // Assert
        assertEquals("Мышь по имени Безымянная мышь бежит со скоростью 10", result)
    }

    @Test
    fun testGetMouseInfo_WithDefaultValues() {
        // Arrange
        val mouse = Mouse()

        // Act
        val result = mouse.getMouseInfo()

        // Assert
        assertEquals("Мышь по имени Безымянная мышь бежит со скоростью 0", result)
    }
}