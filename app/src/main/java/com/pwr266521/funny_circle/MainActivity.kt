package com.pwr266521.funny_circle

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.pwr266521.funny_circle.ui.theme.FunnycircleTheme
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private val position = mutableStateOf(Offset(550f, 100f))
    private var isMoving = true
    private val score = mutableStateOf(0)
    private val isCircleVisible = mutableStateOf(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)

        setContent {
            FunnycircleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GameUI(position.value)
                }
            }
        }
    }


    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val sensitivity = 1f
            val xChange = event.values[0] * sensitivity
            val yChange = event.values[1] * sensitivity

            val newX = position.value.x - xChange
            val newY = position.value.y + yChange

            val screenWidth = resources.displayMetrics.widthPixels
            val screenHeight = resources.displayMetrics.heightPixels

            val radius = 95.0

            if (newX - radius < 0 || newX + radius > screenWidth ||
                newY - radius < 0 || newY + radius > screenHeight
            ) {
                score.value += 1
            } else {
                position.value = Offset(newX, newY)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Handle sensor accuracy changes if needed
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.also { acc ->
            sensorManager.registerListener(this, acc, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    @Composable
    fun GameUI(position: Offset) {
        Column {
            Text(text = "Score: ${score.value}", fontSize = 20.sp, color = Color.Black)
            Canvas(modifier = Modifier.fillMaxSize()) {
                val radius = 20.dp.toPx()
                drawCircle(
                    color = Color.Blue,
                    radius = radius,
                    center = position
                )
            }
        }
    }
}



