package ru.ilyamorozov.rats_and_cats.model

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class Cat(val speed: Int = 3, val isChasing: Boolean = true) {
    var x: Float = 0f
    var y: Float = 0f
    var angle: Float = 0f // Для случайных кошек

    fun moveTowards(mouseX: Float, mouseY: Float) {
        if (isChasing) {
            val dx = mouseX - x
            val dy = mouseY - y
            val dist = sqrt((dx * dx + dy * dy).toDouble()).toFloat()
            if (dist > 0) {
                x += (dx / dist) * speed
                y += (dy / dist) * speed
            }
        } else {
            // Движение по прямой для случайных кошек
            x += speed * cos(angle)
            y += speed * sin(angle)
        }
    }
}