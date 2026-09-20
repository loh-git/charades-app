package com.zoomi.charades.data

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class SettingsRepositoryTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private val dataStoreScope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())

    private fun newRepository() = SettingsRepository(
        PreferenceDataStoreFactory.create(scope = dataStoreScope) { File(tempFolder.root, "settings.preferences_pb") },
    )

    @Test
    fun `defaults to sensible values`() = runTest {
        val settings = newRepository().settings.first()

        assertEquals(60, settings.defaultRoundDurationSeconds)
        assertEquals(TiltSensitivity.MEDIUM, settings.tiltSensitivity)
        assertTrue(settings.soundEnabled)
        assertFalse(settings.touchFallbackEnabled)
        assertFalse(settings.invertTilt)
        assertEquals(AppTheme.POP_ART, settings.theme)
        assertTrue(settings.hapticsEnabled)
        assertTrue(settings.fullscreenModeEnabled)
    }

    @Test
    fun `updates persist through the flow`() = runTest {
        val repository = newRepository()

        repository.setRoundDuration(90)
        repository.setTiltSensitivity(TiltSensitivity.HIGH)
        repository.setSoundEnabled(false)
        repository.setInvertTilt(true)
        repository.setTheme(AppTheme.POP_ART)
        repository.setHapticsEnabled(false)
        repository.setFullscreenModeEnabled(false)

        val settings = repository.settings.first()
        assertEquals(90, settings.defaultRoundDurationSeconds)
        assertEquals(TiltSensitivity.HIGH, settings.tiltSensitivity)
        assertFalse(settings.soundEnabled)
        assertTrue(settings.invertTilt)
        assertEquals(AppTheme.POP_ART, settings.theme)
        assertFalse(settings.hapticsEnabled)
        assertFalse(settings.fullscreenModeEnabled)
    }
}
