package ru.ilyamorozov.rats_and_cats

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Random
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

class GameView(context: Context) : View(context) {
    private val paint = Paint().apply { color = Color.BLUE }
    private val mouse = Mouse(speed = 10)
    private val cheeses = mutableListOf<Cheese>()
    private var chasingCat: Cat? = null
    private val strayCats = mutableListOf<Cat>()
    private var score = 0
    private var isGameOver = false
    private val random = Random()
    private var lastStrayCatSpawnTime = 0L
    private var gameLoop: Job? = null
    private var mediaPlayer: MediaPlayer? = null
    private var soundPool: SoundPool? = null
    private var eatCheeseSoundId: Int = 0
    private val prefs = context.getSharedPreferences("game_prefs", Context.MODE_PRIVATE)
    private val viewModel: SharedViewModel by lazy {
        ViewModelProvider(context as FragmentActivity).get(SharedViewModel::class.java)
    }

    // Переменные для хранения скоростей кошек
    private var chasingCatSpeed = 8
    private  var strayCatSpeed = 4

    init {
        if (context is FragmentActivity) {
            viewModel.loadSavedLevel(context)
            // Наблюдение за изменением уровня
            context.lifecycleScope.launch {
                viewModel.selectedLevel.collectLatest { level ->
                    println("Level changed: $level")
                    resetGame() // Перезапускаем игру при смене уровня
                }
            }
        }
        resetGame()
        initAudio()
    }

    private fun initAudio() {
        // Инициализация SoundPool для звуковых эффектов
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()
        eatCheeseSoundId = soundPool?.load(context, R.raw.eat_cheese, 1) ?: 0

        // Инициализация MediaPlayer для фоновой музыки
        if (isMusicEnabled()) {
            mediaPlayer = MediaPlayer.create(context, R.raw.background_music).apply {
                isLooping = true
                try {
                    start()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun isMusicEnabled(): Boolean {
        return prefs.getBoolean("music_enabled", false)
    }

    override fun isSoundEffectsEnabled(): Boolean {
        return prefs.getBoolean("sound_effects_enabled", true)
    }

    private fun resetGame() {
        score = 0
        isGameOver = false
        cheeses.clear()
        strayCats.clear()

        // Начальная позиция мыши
        mouse.x = width / 2f
        mouse.y = height / 2f

        // Получаем уровень из ViewModel и вычисляем скорости ОДИН РАЗ
        val level = viewModel.selectedLevel.value
        val speeds = when (level?.difficulty) {
            "Легкий" -> Pair(5, 8)    // Согласованные значения
            "Средний" -> Pair(8, 15)   // Согласованные значения
            "Сложный" -> Pair(15, 25)  // Согласованные значения
            else -> Pair(8, 15) // По умолчанию Средний
        }

        // Сохраняем скорости в переменные класса
        chasingCatSpeed = speeds.first
        strayCatSpeed = speeds.second
        Log.i("RatsAndCats", "Chasing cat speed: $chasingCatSpeed, Stray cat speed: $strayCatSpeed")

        // Инициализация основной кошки
        chasingCat = Cat(speed = chasingCatSpeed, isChasing = true).apply {
            val corner = random.nextInt(4)
            when (corner) {
                0 -> { x = 50f; y = 50f }
                1 -> { x = width - 50f; y = 50f }
                2 -> { x = 50f; y = height - 50f }
                3 -> { x = width - 50f; y = height - 50f }
            }
        }

        // Спавн сыра в начале игры
        spawnCheese()
        updateScoreNotification()
        startGameLoop()
    }

    private fun startGameLoop() {
        gameLoop?.cancel()
        gameLoop = CoroutineScope(Dispatchers.Main).launch {
            while (!isGameOver) {
                spawnCheese()
                spawnStrayCat()
                updateGame()
                invalidate()
                delay(16)
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawCircle(mouse.x, mouse.y, 20f, paint.apply { color = Color.GRAY })
        cheeses.forEach { cheese ->
            if (!cheese.isEaten) canvas.drawCircle(cheese.x, cheese.y, 15f, paint.apply { color = Color.YELLOW })
        }
        chasingCat?.let { cat ->
            canvas.drawCircle(cat.x, cat.y, 25f, paint.apply { color = Color.RED })
        }
        strayCats.forEach { cat ->
            canvas.drawCircle(cat.x, cat.y, 25f, paint.apply { color = Color.MAGENTA })
        }
        canvas.drawText("Счет: $score", 10f, 30f, paint.apply { color = Color.BLACK; textSize = 30f })
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_MOVE, MotionEvent.ACTION_DOWN -> {
                mouse.moveTo(event.x, event.y)
                invalidate()
            }
        }
        return true
    }

    private fun spawnCheese() {
        if (random.nextInt(100) < 5 && cheeses.size < 5) {
            val cheese = Cheese()
            do {
                cheese.x = random.nextFloat() * width
                cheese.y = random.nextFloat() * height
            } while (abs(cheese.x - mouse.x) < 50 && abs(cheese.y - mouse.y) < 50)
            cheeses.add(cheese)
        }
    }

    private fun spawnStrayCat() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastStrayCatSpawnTime > random.nextInt(4000) + 3000 && strayCats.size < 3) {
            // Используем уже вычисленную скорость
            val cat = Cat(speed = strayCatSpeed, isChasing = false)
            cat.x = if (random.nextBoolean()) 0f else width.toFloat()
            cat.y = random.nextFloat() * height
            cat.angle = random.nextFloat() * 2 * Math.PI.toFloat()
            strayCats.add(cat)
            lastStrayCatSpawnTime = currentTime
        }
    }

    private fun updateGame() {
        chasingCat?.moveTowards(mouse.x, mouse.y)
        strayCats.forEach { cat ->
            cat.x += cat.speed * cos(cat.angle)
            cat.y += cat.speed * sin(cat.angle)
        }
        strayCats.removeIf { cat ->
            cat.x < -25f || cat.x > width + 25f || cat.y < -25f || cat.y > height + 25f
        }
        cheeses.removeIf { cheese ->
            if (!cheese.isEaten && isCollision(mouse, cheese)) {
                cheese.isEaten = true
                score += 10
                if (isSoundEffectsEnabled()) {
                    soundPool?.play(eatCheeseSoundId, 1f, 1f, 1, 0, 1f)
                }
                animateCheeseDisappear(cheese)
                updateScoreNotification()
                true
            } else false
        }
        // Проверка коллизии с преследующей кошкой
        chasingCat?.let { cat ->
            if (isCollision(mouse, cat)) {
                endGame()
                return // Выходим, чтобы не проверять другие кошки
            }
        }

        // Проверка коллизий с бродячими кошками (только если игра не окончена)
        if (!isGameOver) {
            strayCats.forEach { cat ->
                if (isCollision(mouse, cat)) {
                    endGame()
                    return // Выходим после первой коллизии
                }
            }
        }
    }

    private fun isCollision(a: Any, b: Any): Boolean {
        val ax = if (a is Mouse) a.x else if (a is Cheese) a.x else if (a is Cat) a.x else 0f
        val ay = if (a is Mouse) a.y else if (a is Cheese) a.y else if (a is Cat) a.y else 0f
        val bx = if (b is Mouse) b.x else if (b is Cheese) b.x else if (b is Cat) b.x else 0f
        val by = if (b is Mouse) b.y else if (b is Cheese) b.y else if (b is Cat) b.y else 0f
        return Math.abs(ax - bx) < 30 && Math.abs(ay - by) < 30
    }

    private fun animateCheeseDisappear(cheese: Cheese) {
        ValueAnimator.ofFloat(1f, 0f).apply {
            duration = 500
            addUpdateListener { anim ->
                invalidate()
            }
            start()
        }
    }

    private fun updateScoreNotification() {
        val intent = Intent(context, GameService::class.java).apply {
            putExtra("score", score)
        }
        ContextCompat.startForegroundService(context, intent)
    }

    private fun stopScoreNotification() {
        val intent = Intent(context, GameService::class.java)
        context.stopService(intent)
    }

    fun pause() {
        gameLoop?.cancel()
        mediaPlayer?.pause()
    }

    fun resume() {
        if (isGameOver) {
            resetGame()
        } else {
            startGameLoop()
            if (isMusicEnabled()) {
                mediaPlayer?.start()
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (mouse.x == 0f || mouse.y == 0f) {
            mouse.x = w / 2f
            mouse.y = h / 2f
        }
        if (chasingCat?.x == 0f || chasingCat?.y == 0f) {
            chasingCat?.x = 50f
            chasingCat?.y = 50f
        }
    }

    fun cleanup() {
        gameLoop?.cancel()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        soundPool?.release()
        soundPool = null
        stopScoreNotification()
    }

    private fun endGame() {
        if (isGameOver) return // Избегаем множественных вызовов

        isGameOver = true
        gameLoop?.cancel()
        stopScoreNotification()

        // Показываем диалог
        (context as? MainActivity)?.let { activity ->
            EndGameDialogFragment.newInstance(score).show(activity.supportFragmentManager, "end_game")
        }
    }
}