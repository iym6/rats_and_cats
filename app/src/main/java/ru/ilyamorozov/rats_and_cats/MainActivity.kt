package ru.ilyamorozov.rats_and_cats

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewModel: SharedViewModel by viewModels()
        viewModel.loadSavedLevel(this)

        setupStartButton()
        setupNavigationButtons()
        setupLeaderboardButton()
    }

    private fun setupStartButton() {
        findViewById<Button>(R.id.startButton).setOnClickListener {
            supportFragmentManager.commit {
                replace(R.id.fragmentContainer, GameFragment())
                addToBackStack(null)
            }
        }
    }

    private fun setupNavigationButtons() {
        findViewById<Button>(R.id.levelsButton).setOnClickListener {
            supportFragmentManager.commit {
                replace(R.id.fragmentContainer, LevelsFragment())
                addToBackStack(null)
            }
        }

        findViewById<Button>(R.id.settingsButton).setOnClickListener {
            supportFragmentManager.commit {
                replace(R.id.fragmentContainer, SettingsFragment())
                addToBackStack(null)
            }
        }
    }

    private fun setupLeaderboardButton() {
        findViewById<Button>(R.id.leaderboardButton).setOnClickListener {
            supportFragmentManager.commit {
                replace(R.id.fragmentContainer, LeaderboardFragment())
                addToBackStack(null)
            }
        }
    }
}