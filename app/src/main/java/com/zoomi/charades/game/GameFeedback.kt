package com.zoomi.charades.game

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.exp
import kotlin.math.sin

private const val SAMPLE_RATE = 44100

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

    // Precomputed once — regenerating the waveform on every guess would be wasted work for a
    // fixed, deterministic sound.
    private val correctSamples: ShortArray by lazy { generateCorrectDing() }
    private val incorrectSamples: ShortArray by lazy { generateIncorrectBuzz() }

    fun onEvent(event: GameEvent) {
        when (event) {
            GameEvent.CORRECT -> {
                vibrate(120)
                playPcm(correctSamples)
            }
            GameEvent.PASS -> {
                vibrate(60)
                playPcm(incorrectSamples)
            }
            GameEvent.TIME_UP -> {
                vibrate(400)
                playTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 400)
            }
        }
    }

    // A high-pitched, pleasant two-tone "Ding-Ding!": C5 (523.25 Hz) immediately followed by
    // E5 (659.25 Hz), sine wave, 0.25s total, each note decaying quickly so the transition
    // between the two notes doesn't click.
    private fun generateCorrectDing(): ShortArray {
        val samplesPerNote = (SAMPLE_RATE * 0.125).toInt()
        val totalSamples = samplesPerNote * 2
        val noteDurationSeconds = samplesPerNote / SAMPLE_RATE.toDouble()
        val buffer = ShortArray(totalSamples)
        val frequencies = doubleArrayOf(523.25, 659.25)
        for (i in 0 until totalSamples) {
            val noteIndex = i / samplesPerNote
            val tWithinNote = (i % samplesPerNote) / SAMPLE_RATE.toDouble()
            val envelope = exp(-tWithinNote / (noteDurationSeconds * 0.25))
            val sample = sin(2 * PI * frequencies[noteIndex] * tWithinNote) * envelope
            buffer[i] = (sample * Short.MAX_VALUE * 0.8).toInt().toShort()
        }
        return buffer
    }

    // A lower, soft "Buzzer": triangle wave sliding from G3 (196 Hz) down to E3 (164.8 Hz) over
    // 0.3s with a quick dampening fade out.
    private fun generateIncorrectBuzz(): ShortArray {
        val duration = 0.3
        val totalSamples = (SAMPLE_RATE * duration).toInt()
        val buffer = ShortArray(totalSamples)
        val freqStart = 196.0
        val freqEnd = 164.8
        var phase = 0.0
        for (i in 0 until totalSamples) {
            val t = i / SAMPLE_RATE.toDouble()
            val progress = t / duration
            val freq = freqStart + (freqEnd - freqStart) * progress
            phase += 2 * PI * freq / SAMPLE_RATE
            if (phase > 2 * PI) phase -= 2 * PI
            val triangle = (2.0 / PI) * asin(sin(phase))
            // Softer decay + hotter peak than the correct-ding envelope: a G3/E3 fundamental is
            // near the bottom of what small phone speakers reproduce at all, so this tone needs
            // more headroom just to be audible where the ding (at a much more speaker-friendly
            // pitch) doesn't.
            val envelope = exp(-t / (duration * 0.6))
            val sample = triangle * envelope
            buffer[i] = (sample * Short.MAX_VALUE * 0.95).toInt().toShort()
        }
        return buffer
    }

    private fun playPcm(samples: ShortArray) {
        if (!soundEnabled) return
        val audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build(),
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(SAMPLE_RATE)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build(),
            )
            .setBufferSizeInBytes(samples.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()
        audioTrack.write(samples, 0, samples.size)
        audioTrack.setNotificationMarkerPosition(samples.size)
        audioTrack.setPlaybackPositionUpdateListener(object : AudioTrack.OnPlaybackPositionUpdateListener {
            override fun onMarkerReached(track: AudioTrack) {
                track.release()
            }
            override fun onPeriodicNotification(track: AudioTrack) = Unit
        })
        audioTrack.setVolume(1f)
        audioTrack.play()
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
