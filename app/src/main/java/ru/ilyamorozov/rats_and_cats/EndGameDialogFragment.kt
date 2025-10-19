package ru.ilyamorozov.rats_and_cats

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EndGameDialogFragment : DialogFragment() {

    private val viewModel: LeaderboardViewModel by viewModels()
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
        val nameEditText: EditText = view.findViewById(R.id.nameEditText)
        val saveButton: Button = view.findViewById(R.id.saveButton)

        saveButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val date = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())
            val record = ScoreRecord(playerName = name, score = score, date = date)
            viewModel.insertScore(record)
            dismiss()
        }

        return AlertDialog.Builder(requireContext())
            .setTitle("Игра окончена! Счет: $score")
            .setView(view)
            .create()
    }
}