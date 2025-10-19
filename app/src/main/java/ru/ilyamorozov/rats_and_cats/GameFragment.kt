package ru.ilyamorozov.rats_and_cats

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import android.view.WindowManager

class GameFragment : Fragment() {
    private lateinit var gameView: GameView
    private val viewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        gameView = GameView(requireContext())
        return gameView
    }

    override fun onResume() {
        super.onResume()
        // Включаем полноэкранный режим
        requireActivity().window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        // Скрываем меню
        with(requireActivity()) {
            findViewById<View>(R.id.titleTextView)?.visibility = View.GONE
            findViewById<View>(R.id.startButton)?.visibility = View.GONE
            findViewById<View>(R.id.levelsButton)?.visibility = View.GONE
            findViewById<View>(R.id.settingsButton)?.visibility = View.GONE
            findViewById<View>(R.id.leaderboardButton)?.visibility = View.GONE
        }
        // Выводим в консоль выбранный уровень
        val selectedLevel = viewModel.selectedLevel.value
        println("Starting game with level: $selectedLevel")
        // Запускаем сервис
        val intent = Intent(context, GameService::class.java).apply {
            putExtra("score", 0)
        }
        ContextCompat.startForegroundService(requireContext(), intent)
        gameView.resume()
    }

    override fun onPause() {
        super.onPause()
        // Выключаем полноэкранный режим
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        // Останавливаем сервис
        val intent = Intent(context, GameService::class.java)
        requireContext().stopService(intent)
        gameView.pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        gameView.cleanup()
    }
}