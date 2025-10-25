package ru.ilyamorozov.rats_and_cats.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import ru.ilyamorozov.rats_and_cats.R
import ru.ilyamorozov.rats_and_cats.ui.fragment.EmptyNicknameDialogFragment
import ru.ilyamorozov.rats_and_cats.ui.fragment.GameFragment
import ru.ilyamorozov.rats_and_cats.ui.fragment.LeaderboardFragment
import ru.ilyamorozov.rats_and_cats.ui.fragment.LevelsFragment
import ru.ilyamorozov.rats_and_cats.ui.fragment.SettingsFragment
import ru.ilyamorozov.rats_and_cats.viewmodel.SharedViewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel.loadSavedLevel(this)
        setMenuVisibility(true) // Показываем меню при запуске

        setupButtons()

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

    private fun setupButtons() {
        // Загружаем анимации
        val scale = AnimationUtils.loadAnimation(this, R.anim.scale_button)
        val scaleReverse = AnimationUtils.loadAnimation(this, R.anim.scale_button_reverse)

        // Функция для применения анимации к кнопке
        @SuppressLint("ClickableViewAccessibility")
        fun View.applyClickAnimation() {
            setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> v.startAnimation(scale)
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> v.startAnimation(scaleReverse)
                }
                false // Не поглощаем событие, чтобы onClick сработал
            }
        }

        // Настройка кнопок
        findViewById<Button>(R.id.startButton).apply {
            applyClickAnimation()
            setOnClickListener { showFragment(GameFragment()) }
        }
        findViewById<Button>(R.id.levelsButton).apply {
            applyClickAnimation()
            setOnClickListener { showFragment(LevelsFragment()) }
        }
        findViewById<Button>(R.id.settingsButton).apply {
            applyClickAnimation()
            setOnClickListener { showFragment(SettingsFragment()) }
        }
        findViewById<Button>(R.id.leaderboardButton).apply {
            applyClickAnimation()
            setOnClickListener { showFragment(LeaderboardFragment()) }
        }
    }

    // Метод для показа фрагмента с управлением видимостью меню
    fun showFragment(fragment: Fragment) {
        Log.i("RatsAndCats", "Showing fragment: ${fragment.javaClass.simpleName}")

        // Проверяем ник только для GameFragment
        if (fragment is GameFragment) {
            val prefs = getSharedPreferences("game_prefs", MODE_PRIVATE)
            val playerName = prefs.getString("player_name", "")?.trim()
            if (playerName.isNullOrEmpty()) {
                Log.i("RatsAndCats", "Empty nickname detected, showing EmptyNicknameDialogFragment")
                EmptyNicknameDialogFragment().show(supportFragmentManager, "empty_nickname")
                return
            }
        }

        setMenuVisibility(false)
        supportFragmentManager.commit {
            replace(R.id.fragment_container, fragment)
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