package ru.ilyamorozov.rats_and_cats

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

class GameFragment : Fragment() {

    private lateinit var gameView: GameView
    private val sharedViewModel: SharedViewModel by activityViewModels()
    // private var mediaPlayer: MediaPlayer? = null
    // private var soundPool: SoundPool? = null
    // private var eatSoundId: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        gameView = GameView(requireContext())
        return gameView
    }

    override fun onResume() {
        super.onResume()
        /*
        val prefs = requireActivity().getSharedPreferences("game_prefs", Context.MODE_PRIVATE)
        if (prefs.getBoolean("music_enabled", false)) {
            mediaPlayer = MediaPlayer.create(context, R.raw.background_music)
            mediaPlayer?.isLooping = true
            mediaPlayer?.start()
        }

        soundPool = SoundPool.Builder().build()
        eatSoundId = soundPool?.load(context, R.raw.eat_sound, 1) ?: 0
        */

        // Start service
        val intent = Intent(context, GameService::class.java)
        requireContext().startForegroundService(intent)
    }

    override fun onPause() {
        super.onPause()
        /*
        mediaPlayer?.stop()
        mediaPlayer?.release()
        soundPool?.release()
        */
        gameView.pause()

        // Stop service
        val intent = Intent(context, GameService::class.java)
        requireContext().stopService(intent)
    }

    // Вызов soundPool?.play(eatSoundId, 1f, 1f, 1, 0, 1f) при поедании сыра (добавить в updateGame)
}