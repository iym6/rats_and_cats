package ru.ilyamorozov.rats_and_cats

class Cat(val speed: Int = 3) {
    var x: Float = 0f
    var y: Float = 0f

    fun moveTowards(mouseX: Float, mouseY: Float) {
        val dx = mouseX - x
        val dy = mouseY - y
        val dist = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()
        if (dist > 0) {
            x += (dx / dist) * speed
            y += (dy / dist) * speed
        }
    }
}