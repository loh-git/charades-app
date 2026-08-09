package com.zoomi.charades.game

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/** Bundles the haptic + sound feedback fired on correct/pass/time-up game events. */
class GameFeedback(context: Context) {

    var soundEnabled: Boolean = true

    private val vibrator: Vibrator =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            manager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

    private val toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 80)

    fun onEvent(event: GameEvent) {
        when (event) {
            GameEvent.CORRECT -> {
                vibrate(120)
                playTone(ToneGenerator.TONE_PROP_BEEP2, 150)
            }
            GameEvent.PASS -> {
                vibrate(60)
                playTone(ToneGenerator.TONE_PROP_BEEP, 150)
            }
            GameEvent.TIME_UP -> {
                vibrate(400)
                playTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 400)
            }
        }
    }

    private fun playTone(tone: Int, durationMillis: Int) {
        if (soundEnabled) {
            toneGenerator.startTone(tone, durationMillis)
        }
    }

    private fun vibrate(durationMillis: Long) {
        vibrator.vibrate(VibrationEffect.createOneShot(durationMillis, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    fun release() {
        toneGenerator.release()
    }
}
