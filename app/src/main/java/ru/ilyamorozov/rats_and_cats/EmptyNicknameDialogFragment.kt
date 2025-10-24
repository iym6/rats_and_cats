package ru.ilyamorozov.rats_and_cats

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class EmptyNicknameDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_empty_nickname, null)

        val messageText: TextView = view.findViewById(R.id.messageText)
        val settingsButton: Button = view.findViewById(R.id.settingsButton)
        val cancelButton: Button = view.findViewById(R.id.cancelButton)

        messageText.text = "Пожалуйста, введите ник в настройках!"

        settingsButton.setOnClickListener {
            dismiss()
            (requireActivity() as? MainActivity)?.showFragment(SettingsFragment())
        }

        cancelButton.setOnClickListener {
            dismiss()
            (requireActivity() as? MainActivity)?.showMainMenu()
        }

        return AlertDialog.Builder(requireContext(), R.style.CustomDialogTheme)
            .setView(view)
            .setCancelable(false)
            .create().apply {
                window?.setBackgroundDrawableResource(android.R.color.transparent)
            }
    }
}