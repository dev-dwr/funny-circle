package com.pwr266521.funny_circle

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import com.pwr266521.funny_circle.ui.theme.FunnycircleTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pwr266521.funny_circle.Constants.WINNING_SCORE
import com.pwr266521.funny_circle.Util.calculateNewPosition
import com.pwr266521.funny_circle.Util.isCollision

class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    private val position = mutableStateOf(Offset(550f, 100f))
    private val score = mutableStateOf(0)
    private val radius = mutableStateOf(95.0)
    private val isWallHit = mutableStateOf(false)

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
                    GameUI(position.value, score.value)
                }
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type != Sensor.TYPE_ACCELEROMETER) return

        val screenWidth = resources.displayMetrics.widthPixels
        val screenHeight = resources.displayMetrics.heightPixels

        val (newX, newY) = calculateNewPosition(event, position.value)

        if (isCollision(newX, newY, screenWidth, screenHeight, radius.value)) {
            handleWallCollision()
        } else {
            updatePosition(newX, newY)
        }
    }

    private fun updatePosition(newX: Float, newY: Float) {
        position.value = Offset(newX, newY)
        isWallHit.value = false
    }

    private fun handleWallCollision() {
        if (!isWallHit.value) {
            score.value += 1
            isWallHit.value = true
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

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
    fun GameUI(position: Offset, score: Int) {
        if (score >= WINNING_SCORE) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(stringResource(id = R.string.notification), fontSize = 24.sp, color = Color.Green)
                Button(onClick = { resetGame() }) {
                    Text(stringResource(id = R.string.play_again))
                }
            }

        } else {
            Column {
                Text(text = "Score: $score", fontSize = 20.sp, color = Color.Black)
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

    private fun resetGame() {
        score.value = 0
        position.value = Offset(550f, 100f)
        isWallHit.value = false
    }
}



