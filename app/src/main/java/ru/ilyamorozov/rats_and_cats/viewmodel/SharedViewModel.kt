package ru.ilyamorozov.rats_and_cats.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.ilyamorozov.rats_and_cats.model.Level

class SharedViewModel : ViewModel() {
    private val _selectedLevel = MutableStateFlow<Level?>(null)
    val selectedLevel: StateFlow<Level?> = _selectedLevel

    fun selectLevel(level: Level, context: Context? = null) {
        _selectedLevel.value = level
        // Сохраняем в SharedPreferences
        context?.let {
            val prefs = it.getSharedPreferences("game_prefs", Context.MODE_PRIVATE)
            with(prefs.edit()) {
                putInt("selected_level_id", level.id)
                putString("selected_level_name", level.name)
                putString("selected_level_difficulty", level.difficulty)
                apply()
            }
        }
    }

    fun loadSavedLevel(context: Context) {
        val prefs = context.getSharedPreferences("game_prefs", Context.MODE_PRIVATE)
        val id = prefs.getInt("selected_level_id", -1)
        if (id != -1) {
            val name = prefs.getString("selected_level_name", "") ?: ""
            val difficulty = prefs.getString("selected_level_difficulty", "") ?: ""
            _selectedLevel.value = Level(id, name, difficulty)
        }
    }
}