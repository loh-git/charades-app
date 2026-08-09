package com.zoomi.charades.game

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager

fun Context.hasAccelerometer(): Boolean {
    val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    return sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null
}
