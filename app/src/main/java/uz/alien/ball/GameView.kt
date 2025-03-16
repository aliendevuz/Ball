package uz.alien.ball

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.Surface
import android.view.View
import android.view.WindowManager


class GameView(context: Context, attrs: AttributeSet? = null) : View(context, attrs), SensorEventListener {

  private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
  private var accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
  private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

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
  private var speedX = 0.0f
  private val ballRadius = 50f

  private var w = 0f
  private var h = 0f

  private val handler = Handler(Looper.getMainLooper())
  private val frameRate = 16L

  private val gameLoop = object : Runnable {
    override fun run() {
      updatePhysics()
      invalidate()
      handler.postDelayed(this, frameRate)
    }
  }

  fun startSensor() {
    accelerometer?.let {
      sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
    }
  }

  init {
    handler.post(gameLoop)
    accelerometer?.let {
      sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
    }
  }

  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    super.onSizeChanged(w, h, oldw, oldh)
    this.w = w.toFloat()
    this.h = h.toFloat()
    ballX = this.w / 2
    ballY = ballRadius * 4
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

    speedX /= 1.2f
    ballX += speedX

    if (ballX < ballRadius) {
      ballX = ballRadius
      speedX = -speedX
    }
    if (ballX > w - ballRadius) {
      ballX = w - ballRadius
      speedX = -speedX
    }
    if (ballY > h - ballRadius * 4) {
      ballY = h - ballRadius * 4
      potentialEnergy = -potentialEnergy * elastic
    } else {
      potentialEnergy += 0.1f
    }
  }

  override fun onTouchEvent(event: MotionEvent): Boolean {
    if (event.action == MotionEvent.ACTION_DOWN) {
      potentialEnergy = -16.0f  // To‘pni sakratish
    }
    return true
  }

  override fun onSensorChanged(event: SensorEvent?) {
    if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
      val rotation = windowManager.defaultDisplay.rotation
      val rawX = event.values[0] // Chap-o‘ng og‘ish
      val rawY = event.values[1] // Oldinga-orqaga og‘ish

      val tiltX = when (rotation) {
        Surface.ROTATION_0 -> rawX  // Normal portret
        Surface.ROTATION_90 -> -rawY  // Gorizontal chap
        Surface.ROTATION_180 -> -rawX // Teskarisiga portret
        Surface.ROTATION_270 -> rawY // Gorizontal o‘ng
        else -> -rawX
      }

      speedX -= tiltX * 1.5f
    }
  }

  override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

  fun releaseResources() {
    sensorManager.unregisterListener(this)
  }
}