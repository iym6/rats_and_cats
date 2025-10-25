package ru.ilyamorozov.rats_and_cats.ui.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import ru.ilyamorozov.rats_and_cats.ui.activity.MainActivity
import ru.ilyamorozov.rats_and_cats.R
import ru.ilyamorozov.rats_and_cats.data.local.ScoreRecord
import ru.ilyamorozov.rats_and_cats.viewmodel.LeaderboardViewModel

class EndGameDialogFragment : DialogFragment() {
    private val leaderboardViewModel: LeaderboardViewModel by activityViewModels()
    private var score: Int = 0

    companion object {
        fun newInstance(score: Int): EndGameDialogFragment {
            val fragment = EndGameDialogFragment()
            fragment.arguments = Bundle().apply { putInt("score", score) }
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        score = arguments?.getInt("score", 0) ?: 0
    }

    @SuppressLint("SetTextI18n", "UseGetLayoutInflater")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view: View = LayoutInflater.from(context).inflate(R.layout.dialog_end_game, null)

        val scoreText: TextView = view.findViewById(R.id.scoreText)
        val playAgainButton: Button = view.findViewById(R.id.playAgainButton)
        val exitButton: Button = view.findViewById(R.id.exitButton)
        scoreText.text = getString(R.string.score) + ": $score"
        saveRecordAutomatically()

        playAgainButton.setOnClickListener {
            val gameFragment =
                parentFragmentManager.findFragmentById(R.id.fragment_container) as? GameFragment
            gameFragment?.resetGame()
            dismiss()
        }

        exitButton.setOnClickListener {
            dismiss()
            (requireActivity() as? MainActivity)?.showMainMenu()
        }

        return AlertDialog.Builder(requireContext(), R.style.CustomDialogTheme)
            .setView(view)
            .setCancelable(false)
            .create()
    }

    //Автоматическое сохранение рекорда по имени из настроек
    private fun saveRecordAutomatically() {
        val prefs = requireActivity().getSharedPreferences(
            "game_prefs",
            android.content.Context.MODE_PRIVATE
        )
        val playerName = prefs.getString("player_name", null)?.trim()
        if (playerName.isNullOrEmpty()) {
            Log.e("RatsAndCats", "Attempt to save record with empty nickname")
            return // Не сохраняем рекорд, если ник пустой
        }
        val date = java.text.SimpleDateFormat("dd.MM.yyyy", java.util.Locale.getDefault())
            .format(java.util.Date())

        val record = ScoreRecord(
            playerName = playerName,
            score = score,
            date = date
        )

        leaderboardViewModel.saveOrUpdateScore(record)
    }
}