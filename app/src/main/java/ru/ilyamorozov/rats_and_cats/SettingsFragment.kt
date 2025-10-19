package ru.ilyamorozov.rats_and_cats

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {
    private val viewModel: SharedViewModel by activityViewModels()
    private lateinit var musicSwitch: Switch
    private lateinit var playerNameEditText: EditText
    private lateinit var selectedLevelText: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        musicSwitch = view.findViewById(R.id.musicSwitch)
        playerNameEditText = view.findViewById(R.id.playerNameEditText)
        selectedLevelText = view.findViewById(R.id.selectedLevelText)

        val prefs = requireActivity().getSharedPreferences("game_prefs", Context.MODE_PRIVATE)
        musicSwitch.isChecked = prefs.getBoolean("music_enabled", false)
        playerNameEditText.setText(prefs.getString("player_name", "Игрок"))

        // Наблюдение за выбранным уровнем
        lifecycleScope.launch {
            viewModel.selectedLevel.collectLatest { level ->
                selectedLevelText.text = level?.let { "Выбран: ${it.name} (${it.difficulty})" } ?: "Уровень не выбран"
            }
        }

        return view
    }

    override fun onPause() {
        super.onPause()
        val prefs = requireActivity().getSharedPreferences("game_prefs", Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putBoolean("music_enabled", musicSwitch.isChecked)
            putString("player_name", playerNameEditText.text.toString())
            apply()
        }
    }
}