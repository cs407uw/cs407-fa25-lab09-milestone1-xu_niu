package com.cs407.lab09

import android.hardware.Sensor
import android.hardware.SensorEvent
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BallViewModel : ViewModel() {

    private var ball: Ball? = null
    private var lastTimestamp: Long = 0L

    // Expose the ball's position as a StateFlow
    private val _ballPosition = MutableStateFlow(Offset.Zero)
    val ballPosition: StateFlow<Offset> = _ballPosition.asStateFlow()

    /**
     * Called by the UI when the game field's size is known.
     */
    fun initBall(fieldWidth: Float, fieldHeight: Float, ballSizePx: Float) {
        if (ball == null) {
            // TODO: Initialize the ball instance
             ball = Ball(
                 backgroundHeight = fieldHeight,
                 backgroundWidth = fieldWidth,
                 ballSize = ballSizePx
             )

            // TODO: Update the StateFlow with the initial position
             _ballPosition.value = Offset(ball!!.posX, ball!!.posY)
        }
    }

    /**
     * Called by the SensorEventListener in the UI.
     */
    fun onSensorDataChanged(event: SensorEvent) {
        // Ensure ball is initialized
        val currentBall = ball ?: return

        if (event.sensor.type == Sensor.TYPE_GRAVITY) {
            if (lastTimestamp != 0L) {
                val NS2S = 1.0f / 1_000_000_000.0f
                val dT = (event.timestamp - lastTimestamp).toFloat() * NS2S

                val sensorX = event.values[0]
                val sensorY = event.values[1]

                val xAcc = sensorX
                val yAcc = -sensorY

                currentBall.updatePositionAndVelocity(
                    xAcc = xAcc,
                    yAcc = yAcc,
                    dT = dT
                )
                currentBall.checkBoundaries()

                _ballPosition.update {
                    Offset(x = currentBall.posX, y = currentBall.posY)
                }
            }

            lastTimestamp = event.timestamp
        }
    }


    fun reset() {
        // Reset the ball's state
        ball?.reset()

        // Update the StateFlow with the reset position
        ball?.let { currentBall ->
            _ballPosition.value = Offset(currentBall.posX, currentBall.posY)
        }

        // Reset the lastTimestamp
        lastTimestamp = 0L
    }

}