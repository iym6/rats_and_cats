package ru.ilyamorozov.rats_and_cats.model

class Mouse(val name: String = "", val speed: Int = 5) {
    var x: Float = 0f
    var y: Float = 0f

    fun getMouseInfo(): String {
        return "Мышь по имени $name бежит со скоростью $speed"
    }

    fun moveTo(touchX: Float, touchY: Float) {
        x = touchX
        y = touchY
    }
}