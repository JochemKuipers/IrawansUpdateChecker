package com.JochemKuipers.irawansupdatechecker.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.followDataStore: DataStore<Preferences> by preferencesDataStore(name = "followed_roms")

class FollowRepository(private val context: Context) {

    private object Keys {
        val FOLLOWED = stringSetPreferencesKey("followed")
    }

    // Store format: "romKey|lastSeenVersion|displayName"
    private fun FollowedRom.toStorage() = "$romKey|$lastSeenVersion|$displayName"
    private fun String.toFollowedRom(): FollowedRom? {
        val parts = split("|", limit = 4)
        return if (parts.size >= 3) FollowedRom(
            romKey = parts[0],
            lastSeenVersion = parts[1],
            displayName = parts.subList(2, parts.size).joinToString("|")
        ) else null
    }

    val followedRoms: Flow<List<FollowedRom>> = context.followDataStore.data.map { prefs ->
        (prefs[Keys.FOLLOWED] ?: emptySet())
            .mapNotNull { it.toFollowedRom() }
    }

    val followedRomKeys: Flow<Set<String>> = followedRoms.map { it.map { fr -> fr.romKey }.toSet() }

    suspend fun follow(romKey: String, currentVersion: String, displayName: String) {
        context.followDataStore.edit { prefs ->
            val current = prefs[Keys.FOLLOWED] ?: emptySet()
            val updated = current.filter { !it.startsWith("$romKey|") }.toMutableSet()
            updated.add(FollowedRom(romKey, currentVersion, displayName).toStorage())
            prefs[Keys.FOLLOWED] = updated
        }
    }

    suspend fun unfollow(romKey: String) {
        context.followDataStore.edit { prefs ->
            val current = prefs[Keys.FOLLOWED] ?: emptySet()
            prefs[Keys.FOLLOWED] = current.filter { !it.startsWith("$romKey|") }.toSet()
        }
    }

    suspend fun updateLastSeen(romKey: String, version: String, displayName: String) {
        context.followDataStore.edit { prefs ->
            val current = prefs[Keys.FOLLOWED] ?: emptySet()
            val updated = current.filter { !it.startsWith("$romKey|") }.toMutableSet()
            updated.add(FollowedRom(romKey, version, displayName).toStorage())
            prefs[Keys.FOLLOWED] = updated
        }
    }
}
