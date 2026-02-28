package com.JochemKuipers.irawansupdatechecker.data

/**
 * User preferences for background update checks.
 */
data class AppSettings(
    val checkIntervalMinutes: Int = 360,  // default 6 hours
    val notificationsEnabled: Boolean = true
)
