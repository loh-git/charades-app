package com.zoomi.charades.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.zoomi.charades.data.AppTheme
import com.zoomi.charades.data.GameSettings
import com.zoomi.charades.data.SettingsRepository
import com.zoomi.charades.data.TiltSensitivity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val settingsRepository: SettingsRepository) : ViewModel() {

    val settings: StateFlow<GameSettings> = settingsRepository.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), GameSettings())

    fun setRoundDuration(seconds: Int) {
        viewModelScope.launch { settingsRepository.setRoundDuration(seconds) }
    }

    fun setTiltSensitivity(sensitivity: TiltSensitivity) {
        viewModelScope.launch { settingsRepository.setTiltSensitivity(sensitivity) }
    }

    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setSoundEnabled(enabled) }
    }

    fun setTouchFallbackEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setTouchFallbackEnabled(enabled) }
    }

    fun setInvertTilt(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setInvertTilt(enabled) }
    }

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch { settingsRepository.setTheme(theme) }
    }

    class Factory(private val settingsRepository: SettingsRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(settingsRepository) as T
        }
    }
}
