package com.JochemKuipers.irawansupdatechecker.data

/**
 * A ROM the user is following for update notifications.
 */
data class FollowedRom(
    val romKey: String,           // "codename|romName"
    val lastSeenVersion: String,
    val displayName: String       // e.g. "LunarisAOSP for Xiaomi Pad 7 Pro"
)
