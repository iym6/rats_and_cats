package ru.ilyamorozov.rats_and_cats

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit

class MainActivity : AppCompatActivity() {

    private val viewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel.loadSavedLevel(this)

        setupStartButton()
        setupNavigationButtons()
        setupLeaderboardButton()

        // Настройка обработки кнопки "назад" через OnBackPressedDispatcher
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()
                    // Восстанавливаем меню, если возвращаемся на главный экран
                    if (supportFragmentManager.backStackEntryCount == 1) {
                        findViewById<View>(R.id.titleTextView)?.visibility = View.VISIBLE
                        findViewById<View>(R.id.startButton)?.visibility = View.VISIBLE
                        findViewById<View>(R.id.levelsButton)?.visibility = View.VISIBLE
                        findViewById<View>(R.id.settingsButton)?.visibility = View.VISIBLE
                        findViewById<View>(R.id.leaderboardButton)?.visibility = View.VISIBLE
                        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                    }
                } else {
                    finish()
                }
            }
        })
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