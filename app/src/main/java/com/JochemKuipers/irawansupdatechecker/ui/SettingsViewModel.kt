package com.JochemKuipers.irawansupdatechecker.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.JochemKuipers.irawansupdatechecker.data.SettingsRepository
import kotlinx.coroutines.flow.first
import com.JochemKuipers.irawansupdatechecker.worker.WorkScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsRepo = SettingsRepository(application.applicationContext)

    val settings = settingsRepo.settings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = com.JochemKuipers.irawansupdatechecker.data.AppSettings()
    )

    fun setCheckInterval(minutes: Int) {
        viewModelScope.launch {
            settingsRepo.setCheckIntervalMinutes(minutes)
            val s = settingsRepo.settings.first()
            if (s.notificationsEnabled) {
                WorkScheduler.schedule(getApplication(), minutes)
            }
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepo.setNotificationsEnabled(enabled)
            if (enabled) {
                val s = settingsRepo.settings.first()
                WorkScheduler.schedule(getApplication(), s.checkIntervalMinutes)
            } else {
                WorkScheduler.cancel(getApplication())
            }
        }
    }
}
