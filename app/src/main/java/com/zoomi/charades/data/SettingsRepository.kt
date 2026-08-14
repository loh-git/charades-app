package com.zoomi.charades.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class TiltSensitivity(val triggerAngleDegrees: Float, val label: String) {
    LOW(45f, "Low"),
    MEDIUM(35f, "Medium"),
    HIGH(25f, "High"),
}

enum class AppTheme(val label: String, val description: String) {
    DEFAULT("Sunset Arcade", "Warm dark arcade aesthetic with golden amber highlights"),
    LIGHT("Matrix Terminal", "Clean bright layout for daytime play"),
    POP_ART("Pop Art", "Bold primary colours with comic-book energy"),
    TEST("Cyberpunk Neon", "Vivid experimental styling with bigger icons and playful motion"),
    // Not yet visually distinct — reuses the Light theme's colour scheme as a starting point
    // until this gets its own implementation.
    STUDIO_MINIMALIST("Studio Minimalist", "Clean bright layout for daytime play"),
}

data class GameSettings(
    val defaultRoundDurationSeconds: Int = 60,
    val tiltSensitivity: TiltSensitivity = TiltSensitivity.MEDIUM,
    val soundEnabled: Boolean = true,
    val touchFallbackEnabled: Boolean = false,
    val invertTilt: Boolean = false,
    val theme: AppTheme = AppTheme.DEFAULT,
)

private object Keys {
    val ROUND_DURATION = intPreferencesKey("round_duration_seconds")
    val TILT_SENSITIVITY = stringPreferencesKey("tilt_sensitivity")
    val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
    val TOUCH_FALLBACK = booleanPreferencesKey("touch_fallback_enabled")
    val INVERT_TILT = booleanPreferencesKey("invert_tilt")
    val THEME = stringPreferencesKey("app_theme")
}

class SettingsRepository(private val dataStore: DataStore<Preferences>) {

    val settings: Flow<GameSettings> = dataStore.data.map { prefs ->
        GameSettings(
            defaultRoundDurationSeconds = prefs[Keys.ROUND_DURATION] ?: 60,
            tiltSensitivity = prefs[Keys.TILT_SENSITIVITY]
                ?.let { name -> runCatching { TiltSensitivity.valueOf(name) }.getOrNull() }
                ?: TiltSensitivity.MEDIUM,
            soundEnabled = prefs[Keys.SOUND_ENABLED] ?: true,
            touchFallbackEnabled = prefs[Keys.TOUCH_FALLBACK] ?: false,
            invertTilt = prefs[Keys.INVERT_TILT] ?: false,
            theme = prefs[Keys.THEME]
                ?.let { name -> runCatching { AppTheme.valueOf(name) }.getOrNull() }
                ?: AppTheme.DEFAULT,
        )
    }

    suspend fun setRoundDuration(seconds: Int) {
        dataStore.edit { it[Keys.ROUND_DURATION] = seconds }
    }

    suspend fun setTiltSensitivity(sensitivity: TiltSensitivity) {
        dataStore.edit { it[Keys.TILT_SENSITIVITY] = sensitivity.name }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.SOUND_ENABLED] = enabled }
    }

    suspend fun setTouchFallbackEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.TOUCH_FALLBACK] = enabled }
    }

    suspend fun setInvertTilt(enabled: Boolean) {
        dataStore.edit { it[Keys.INVERT_TILT] = enabled }
    }

    suspend fun setTheme(theme: AppTheme) {
        dataStore.edit { it[Keys.THEME] = theme.name }
    }
}
