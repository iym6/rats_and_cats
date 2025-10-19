package ru.ilyamorozov.rats_and_cats

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.View
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Random

class GameView(context: Context) : View(context) {
    private val paint = Paint().apply { color = Color.BLUE }
    private val mouse = Mouse(speed = 10)
    private val cheeses = mutableListOf<Cheese>()
    private val cats = mutableListOf<Cat>()
    private var score = 0
    private var isGameOver = false
    private val random = Random()

    private val gameLoop = CoroutineScope(Dispatchers.Main).launch {
        while (!isGameOver) {
            spawnCheese()
            spawnCat()
            updateGame()
            invalidate() // Перерисовка
            delay(16) // ~60 FPS
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Рисуем мышь
        canvas.drawCircle(mouse.x, mouse.y, 20f, paint.apply { color = Color.GRAY })

        // Рисуем сыры
        cheeses.forEach { cheese ->
            if (!cheese.isEaten) canvas.drawCircle(cheese.x, cheese.y, 15f, paint.apply { color = Color.YELLOW })
        }

        // Рисуем кошек
        cats.forEach { cat ->
            canvas.drawCircle(cat.x, cat.y, 25f, paint.apply { color = Color.RED })
        }

        // Счет
        canvas.drawText("Счет: $score", 10f, 30f, paint.apply { color = Color.BLACK; textSize = 30f })
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                animateMouseTo(event.x, event.y)
            }
        }
        return true
    }

    private fun animateMouseTo(targetX: Float, targetY: Float) {
        ValueAnimator.ofFloat(mouse.x, targetX).apply {
            duration = 200
            addUpdateListener { anim ->
                mouse.x = anim.animatedValue as Float
                invalidate()
            }
            start()
        }
        ValueAnimator.ofFloat(mouse.y, targetY).apply {
            duration = 200
            addUpdateListener { anim ->
                mouse.y = anim.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    private fun spawnCheese() {
        if (random.nextInt(100) < 5 && cheeses.size < 5) { // 5% шанс
            val cheese = Cheese()
            cheese.x = random.nextFloat() * width
            cheese.y = random.nextFloat() * height
            cheeses.add(cheese)
        }
    }

    private fun spawnCat() {
        if (random.nextInt(100) < 2 && cats.size < 3) { // 2% шанс
            val cat = Cat()
            cat.x = if (random.nextBoolean()) 0f else width.toFloat()
            cat.y = random.nextFloat() * height
            cats.add(cat)
        }
    }

    private fun updateGame() {
        // Движение кошек
        cats.forEach { it.moveTowards(mouse.x, mouse.y) }

        // Коллизии с сыром
        cheeses.removeIf { cheese ->
            if (!cheese.isEaten && isCollision(mouse, cheese)) {
                cheese.isEaten = true
                score += 10
                animateCheeseDisappear(cheese)
                true // Удалить после анимации
            } else false
        }

        // Коллизии с кошками
        cats.forEach { cat ->
            if (isCollision(mouse, cat)) {
                isGameOver = true
                (context as? MainActivity)?.let { activity ->
                    EndGameDialogFragment.newInstance(score).show(activity.supportFragmentManager, "end_game")
                }
            }
        }
    }

    private fun isCollision(a: Any, b: Any): Boolean {
        val ax = if (a is Mouse) a.x else if (a is Cheese) a.x else if (a is Cat) a.x else 0f
        val ay = if (a is Mouse) a.y else if (a is Cheese) a.y else if (a is Cat) a.y else 0f
        val bx = if (b is Mouse) b.x else if (b is Cheese) b.x else if (b is Cat) b.x else 0f
        val by = if (b is Mouse) b.y else if (b is Cheese) b.y else if (b is Cat) b.y else 0f
        return Math.abs(ax - bx) < 30 && Math.abs(ay - by) < 30 // Bounding circle approx
    }

    private fun animateCheeseDisappear(cheese: Cheese) {
        ValueAnimator.ofFloat(1f, 0f).apply {
            duration = 500
            addUpdateListener { anim ->
                // Можно масштабировать, но для простоты invalidate
                invalidate()
            }
            start()
        }
    }

    fun pause() {
        gameLoop.cancel()
    }

    fun resume() {
        // Restart loop if needed
    }
}