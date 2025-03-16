package uz.alien.ball

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.Surface
import android.view.View
import android.view.WindowManager

class AntiStress(context: Context, attrs: AttributeSet? = null) : View(context, attrs),
  SensorEventListener {

  private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
  private var accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
  private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

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
  private var speedX = 0.0f
  private var speedY = 0.0f
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

  init {
    handler.post(gameLoop)
    accelerometer?.let {
      sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
    }
  }

  fun startSensor() {
    accelerometer?.let {
      sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
    }
  }

  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    super.onSizeChanged(w, h, oldw, oldh)
    this.w = w.toFloat()
    this.h = h.toFloat()
    ballX = this.w / 2
    ballY = this.h / 2
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    canvas.drawColor(Color.rgb(32, 48, 64))
    canvas.drawCircle(ballX, ballY, ballRadius, ballPaint1)
    canvas.drawCircle(ballX + ballRadius / 2.4f, ballY - ballRadius / 2.4f, ballRadius / 3, ballPaint2)
    canvas.drawCircle(ballX + ballRadius / 2.2f, ballY - ballRadius / 2.2f, ballRadius / 4, ballPaint3)
  }

  private fun updatePhysics() {
    speedX *= 0.98f
    speedY *= 0.98f

    ballX += speedX
    ballY += speedY

    if (ballX < ballRadius) {
      ballX = ballRadius
      speedX = -speedX * 0.8f
    }
    if (ballX > w - ballRadius) {
      ballX = w - ballRadius
      speedX = -speedX * 0.8f
    }
    if (ballY < ballRadius) {
      ballY = ballRadius
      speedY = -speedY * 0.8f
    }
    if (ballY > h - ballRadius) {
      ballY = h - ballRadius
      speedY = -speedY * 0.8f
    }
  }

  override fun onSensorChanged(event: SensorEvent?) {
    if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
      val rotation = windowManager.defaultDisplay.rotation
      val rawX = event.values[0]
      val rawY = event.values[1]

      val tiltX = when (rotation) {
        Surface.ROTATION_0 -> rawX
        Surface.ROTATION_90 -> -rawY
        Surface.ROTATION_180 -> -rawX
        Surface.ROTATION_270 -> rawY
        else -> rawX
      }

      val tiltY = when (rotation) {
        Surface.ROTATION_0 -> rawY
        Surface.ROTATION_90 -> rawX
        Surface.ROTATION_180 -> -rawY
        Surface.ROTATION_270 -> -rawX
        else -> rawY
      }

      speedX -= tiltX * 1.2f
      speedY += tiltY * 1.2f
    }
  }

  override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

  fun releaseResources() {
    sensorManager.unregisterListener(this)
  }
}