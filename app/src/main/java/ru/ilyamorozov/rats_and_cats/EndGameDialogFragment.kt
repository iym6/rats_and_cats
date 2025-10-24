package ru.ilyamorozov.rats_and_cats

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels

class EndGameDialogFragment : DialogFragment() {

    private val viewModel: SharedViewModel by activityViewModels()
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

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view: View = LayoutInflater.from(context).inflate(R.layout.dialog_end_game, null)

        val scoreText: TextView = view.findViewById(R.id.scoreText)
        val titleText: TextView = view.findViewById(R.id.titleText)
        val playAgainButton: Button = view.findViewById(R.id.playAgainButton)
        val exitButton: Button = view.findViewById(R.id.exitButton)
        scoreText.text = "Счет: $score"
        // Автоматическое сохранение рекорда
        saveRecordAutomatically()

        playAgainButton.setOnClickListener {
            // Находим текущий GameFragment и вызываем resetGame
            val gameFragment = parentFragmentManager.findFragmentById(R.id.fragment_container) as? GameFragment
            gameFragment?.resetGame()
            dismiss()
        }

        exitButton.setOnClickListener {
            dismiss()
            (requireActivity() as? MainActivity)?.showMainMenu()
        }

        return AlertDialog.Builder(requireContext(), R.style.CustomDialogTheme)
            .setView(view)
            .setCancelable(false) // Нельзя закрыть без выбора
            .create()
    }

    /** Автоматическое сохранение рекорда по имени из настроек */
    private fun saveRecordAutomatically() {
        val prefs = requireActivity().getSharedPreferences("game_prefs", android.content.Context.MODE_PRIVATE)
        val playerName = prefs.getString("player_name", "Игрок") ?: "Игрок"
        val date = java.text.SimpleDateFormat("dd.MM.yyyy", java.util.Locale.getDefault()).format(java.util.Date())

        val record = ScoreRecord(
            playerName = playerName,
            score = score,
            date = date
        )

        leaderboardViewModel.insertScore(record)
    }
}