package com.pwr266521.funny_circle

import android.hardware.SensorEvent
import androidx.compose.ui.geometry.Offset

object Util {
    fun calculateNewPosition(event: SensorEvent, position: Offset): Pair<Float, Float> {
        val sensitivity = 2f
        val xChange = event.values[0] * sensitivity
        val yChange = event.values[1] * sensitivity

        val newX = position.x - xChange
        val newY = position.y + yChange

        return Pair(newX, newY)
    }

    fun isCollision(newX: Float, newY: Float, screenWidth: Int, screenHeight: Int, radius: Double): Boolean {
        return newX - radius < 0 || newX + radius > screenWidth ||
                newY - radius < 0 || newY + radius > screenHeight
    }
}