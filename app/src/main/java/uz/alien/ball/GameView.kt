package uz.alien.ball

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View


class GameView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

  private val rectPaint = Paint().apply {

    color = Color.rgb(92, 255, 92)
  }
  private val ballPaint1 = Paint().apply {
    style = Paint.Style.FILL
    color = Color.rgb(255, 32, 32)
  }
  private val ballPaint2 = Paint().apply {
    style = Paint.Style.FILL
    color = Color.rgb(255, 128, 128)
  }
  private val ballPaint3 = Paint().apply {
    style = Paint.Style.FILL
    color = Color.rgb(255, 220, 220)
  }

  private var ballX = 200f
  private var ballY = 200f
  private var potentialEnergy = 0.0f
  private var gravitySpeed = 2.0f
  private var elastic = 0.4f
  private var speed = 0.0f
  private val ballRadius = 50f

  private var w = 0f
  private var h = 0f

  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    super.onSizeChanged(w, h, oldw, oldh)
    this.w = w.toFloat()
    this.h = h.toFloat()
    ballX = this.w / 2
    ballY = ballRadius * 4
  }

  private val handler = Handler(Looper.getMainLooper())
  private val frameRate = 16L

  private val gameLoop = object : Runnable {
    override fun run() {
      updatePhysics()
      invalidate()
      handler.postDelayed(this, frameRate)
    }
  }

  init {
    handler.post(gameLoop)
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    canvas.drawColor(Color.rgb(32, 48, 64))

    canvas.drawRect(RectF(0.0f, h - ballRadius * 3, w, h), rectPaint)

    canvas.drawCircle(ballX, ballY, ballRadius, ballPaint1)
    canvas.drawCircle(ballX + ballRadius / 2.4f, ballY - ballRadius / 2.4f, ballRadius / 3, ballPaint2)
    canvas.drawCircle(ballX + ballRadius / 2.2f, ballY - ballRadius / 2.2f, ballRadius / 4, ballPaint3)
  }

  private fun updatePhysics() {

    potentialEnergy += 0.2f
    ballY += gravitySpeed * potentialEnergy

    speed /= 1.2f

    ballX += speed

    if (ballX < ballRadius) ballX = ballRadius
    if (ballX > w - ballRadius) ballX = w - ballRadius
    if (ballY > h - ballRadius * 4) {
      ballY = h - ballRadius * 4
      potentialEnergy = -potentialEnergy * elastic
    } else {
      potentialEnergy += 0.1f
    }
  }
}