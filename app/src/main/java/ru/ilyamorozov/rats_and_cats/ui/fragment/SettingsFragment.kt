package ru.ilyamorozov.rats_and_cats.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.ilyamorozov.rats_and_cats.ui.activity.MainActivity
import ru.ilyamorozov.rats_and_cats.R
import ru.ilyamorozov.rats_and_cats.viewmodel.SharedViewModel

@Suppress("DEPRECATION")
class SettingsFragment : Fragment() {
    private val viewModel: SharedViewModel by activityViewModels()
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var musicSwitch: Switch
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var soundEffectsSwitch: Switch
    private lateinit var playerNameEditText: EditText
    private lateinit var selectedLevelText: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        musicSwitch = view.findViewById(R.id.musicSwitch)
        soundEffectsSwitch = view.findViewById(R.id.soundEffectsSwitch)
        playerNameEditText = view.findViewById(R.id.playerNameEditText)
        selectedLevelText = view.findViewById(R.id.selectedLevelText)

        val prefs = requireActivity().getSharedPreferences("game_prefs", Context.MODE_PRIVATE)
        musicSwitch.isChecked = prefs.getBoolean("music_enabled", false)
        soundEffectsSwitch.isChecked = prefs.getBoolean("sound_effects_enabled", true)
        playerNameEditText.setText(prefs.getString("player_name", ""))

        // Наблюдение за выбранным уровнем
        lifecycleScope.launch {
            viewModel.selectedLevel.collectLatest { level ->
                selectedLevelText.text = level?.let { getString(R.string.chosen) + ": ${it.name} (${it.difficulty})" } ?: getString(R.string.no_level)
            }
        }
        // Обработка нажатия кнопки "Назад"
        view.findViewById<Button>(R.id.backButton)?.setOnClickListener {
            Log.i("RatsAndCats", "Back button clicked in SettingsFragment")
            (requireActivity() as? MainActivity)?.showMainMenu()
        }
        return view
    }

    @SuppressLint("UseKtx")
    override fun onPause() {
        super.onPause()
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        val prefs = requireActivity().getSharedPreferences("game_prefs", Context.MODE_PRIVATE)
        val playerName = playerNameEditText.text.toString().trim()

        if (playerName.isEmpty()) {
            playerNameEditText.error = getString(R.string.not_empty_nickname)
            return
        }

        with(prefs.edit()) {
            putBoolean("music_enabled", musicSwitch.isChecked)
            putBoolean("sound_effects_enabled", soundEffectsSwitch.isChecked)
            putString("player_name", playerName)
            apply()
        }
    }
}