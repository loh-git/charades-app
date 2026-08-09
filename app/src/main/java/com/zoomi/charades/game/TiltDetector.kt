package com.zoomi.charades.game

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.view.Surface
import android.view.WindowManager
import kotlin.math.atan2

/**
 * Detects the forehead-hold "nod" gesture from the accelerometer: tilting the top edge of
 * the screen downward (chin drop) fires [onTiltDown], tilting it upward fires [onTiltUp].
 * The raw accelerometer axes are fixed to the device body, not the screen, so readings are
 * remapped against the current display rotation on [register] to keep "down"/"up" meaning
 * the same whether the round is played in portrait or (as this game locks to) landscape.
 */
class TiltDetector(
    context: Context,
    private val triggerAngleDegrees: Float,
    private val onTiltDown: () -> Unit,
    private val onTiltUp: () -> Unit,
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    // Reset angle scales with the trigger angle so the "must return near neutral before firing
    // again" behavior stays proportional across sensitivity presets.
    private val resetAngleDegrees = triggerAngleDegrees * 0.4f
    private val cooldownMillis = 800L

    private var armed = true
    private var lastTriggerTime = 0L
    private var rotation = Surface.ROTATION_0

    fun register() {
        rotation = currentRotation()
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    fun unregister() {
        sensorManager.unregisterListener(this)
    }

    @Suppress("DEPRECATION")
    private fun currentRotation(): Int = windowManager.defaultDisplay.rotation

    override fun onSensorChanged(event: SensorEvent) {
        // Rotate the raw device-frame accelerometer y-axis into screen-frame based on the
        // display rotation captured at register() — only y feeds the pitch calculation below.
        // z is the axis perpendicular to the screen, which display rotation doesn't touch, so
        // it's carried over unchanged.
        // (SensorManager.remapCoordinateSystem is NOT used here — it expects a 9/16-element
        // rotation matrix, not a raw 3-element vector, and throws IllegalArgumentException.)
        val y = when (rotation) {
            Surface.ROTATION_90 -> event.values[0]
            Surface.ROTATION_180 -> -event.values[1]
            Surface.ROTATION_270 -> -event.values[0]
            else -> event.values[1]
        }
        val z = event.values[2]
        // Neutral hold is upright against the forehead (remapped-vertical y reads ~+9.8, z
        // reads ~0 since z faces out through the screen, roughly horizontal), which makes
        // atan2(-z, y) ~0 at rest. As the top of the phone tips forward/down (away from the
        // player) z goes negative, pushing pitch positive; tipping the top back toward the
        // player (up) makes z positive, pushing pitch negative. (Using atan2(y, z) here was the
        // earlier bug: it put "neutral" at ~90 degrees for this hold, past the trigger
        // threshold immediately, so the first spurious fire could never re-arm.)
        val pitchDegrees = Math.toDegrees(atan2(-z.toDouble(), y.toDouble())).toFloat()

        if (!armed) {
            if (kotlin.math.abs(pitchDegrees) < resetAngleDegrees) {
                armed = true
            }
            return
        }

        val now = System.currentTimeMillis()
        if (now - lastTriggerTime < cooldownMillis) return

        when {
            pitchDegrees > triggerAngleDegrees -> fire(now, onTiltDown)
            pitchDegrees < -triggerAngleDegrees -> fire(now, onTiltUp)
        }
    }

    private fun fire(now: Long, callback: () -> Unit) {
        lastTriggerTime = now
        armed = false
        callback()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}
