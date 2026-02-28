package com.JochemKuipers.irawansupdatechecker.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {

    private object Keys {
        val CHECK_INTERVAL = intPreferencesKey("check_interval_minutes")
        val NOTIFICATIONS_ENABLED = intPreferencesKey("notifications_enabled")  // 1=true, 0=false
    }

    val settings: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            checkIntervalMinutes = prefs[Keys.CHECK_INTERVAL] ?: 360,
            notificationsEnabled = prefs[Keys.NOTIFICATIONS_ENABLED]?.let { it == 1 } ?: true
        )
    }

    suspend fun setCheckIntervalMinutes(minutes: Int) {
        context.dataStore.edit { prefs ->
            prefs[Keys.CHECK_INTERVAL] = minutes.coerceAtLeast(15)
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.NOTIFICATIONS_ENABLED] = if (enabled) 1 else 0
        }
    }
}
