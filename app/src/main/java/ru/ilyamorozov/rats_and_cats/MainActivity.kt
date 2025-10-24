package ru.ilyamorozov.rats_and_cats

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit

class MainActivity : AppCompatActivity() {

    private val viewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel.loadSavedLevel(this)
        setMenuVisibility(true) // Показываем меню при запуске

        setupStartButton()
        setupNavigationButtons()
        setupLeaderboardButton()

        // Слушатель изменений back stack для управления видимостью меню
        supportFragmentManager.addOnBackStackChangedListener {
            val isMainScreen = supportFragmentManager.backStackEntryCount == 0
            Log.i("RatsAndCats", "Back stack changed, count: ${supportFragmentManager.backStackEntryCount}, isMainScreen: $isMainScreen")
            setMenuVisibility(isMainScreen)
        }

        // Настройка обработки кнопки "назад" через OnBackPressedDispatcher
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (supportFragmentManager.backStackEntryCount > 0) {
                    Log.i("RatsAndCats", "Handling back press, popping back stack")
                    supportFragmentManager.popBackStack()
                } else {
                    Log.i("RatsAndCats", "No fragments in back stack, finishing activity")
                    finish()
                }
            }
        })
    }

    private fun setupStartButton() {
        findViewById<Button>(R.id.startButton).setOnClickListener {
            showFragment(GameFragment())
        }
    }

    private fun setupNavigationButtons() {
        findViewById<Button>(R.id.levelsButton).setOnClickListener {
            showFragment(LevelsFragment())
        }

        findViewById<Button>(R.id.settingsButton).setOnClickListener {
            showFragment(SettingsFragment())
        }
    }

    private fun setupLeaderboardButton() {
        findViewById<Button>(R.id.leaderboardButton).setOnClickListener {
            showFragment(LeaderboardFragment())
        }
    }

    // Метод для показа фрагмента с управлением видимостью меню
    fun showFragment(fragment: Fragment) {
        Log.i("RatsAndCats", "Showing fragment: ${fragment.javaClass.simpleName}")
        setMenuVisibility(false) // Скрываем меню для всех фрагментов
        supportFragmentManager.commit {
            replace(R.id.fragmentContainer, fragment)
            addToBackStack(null)
        }
    }

    // Метод для возврата на главный экран
    fun showMainMenu() {
        Log.i("RatsAndCats", "Returning to main menu")
        supportFragmentManager.popBackStack()
    }

    // Метод для управления видимостью меню
    fun setMenuVisibility(visible: Boolean) {
        val visibility = if (visible) View.VISIBLE else View.GONE
        Log.i("RatsAndCats", "Setting menu visibility: $visible")
        findViewById<View>(R.id.titleTextView)?.visibility = visibility
        findViewById<View>(R.id.startButton)?.visibility = visibility
        findViewById<View>(R.id.levelsButton)?.visibility = visibility
        findViewById<View>(R.id.settingsButton)?.visibility = visibility
        findViewById<View>(R.id.leaderboardButton)?.visibility = visibility
    }
}