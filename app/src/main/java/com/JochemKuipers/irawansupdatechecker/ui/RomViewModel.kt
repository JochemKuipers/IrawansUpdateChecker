package com.JochemKuipers.irawansupdatechecker.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.JochemKuipers.irawansupdatechecker.data.DeviceItem
import com.JochemKuipers.irawansupdatechecker.data.FollowRepository
import com.JochemKuipers.irawansupdatechecker.data.RomPost
import com.JochemKuipers.irawansupdatechecker.data.RomRepository
import com.JochemKuipers.irawansupdatechecker.data.SettingsRepository
import com.JochemKuipers.irawansupdatechecker.worker.WorkScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class RomUiState(
    val devices: List<DeviceItem> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null,
    val selectedPost: RomPost? = null
)

class RomViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = RomRepository()
    private val followRepo = FollowRepository(application.applicationContext)
    private val settingsRepo = SettingsRepository(application.applicationContext)

    // Optimistic updates: apply immediately so the star icon toggles without waiting for DataStore
    private val _pendingFollowed = MutableStateFlow<Set<String>>(emptySet())
    private val _pendingUnfollowed = MutableStateFlow<Set<String>>(emptySet())

    val followedRomKeys: StateFlow<Set<String>> = combine(
        followRepo.followedRomKeys,
        _pendingFollowed,
        _pendingUnfollowed
    ) { fromRepo, pendingAdd, pendingRemove ->
        (fromRepo + pendingAdd) - pendingRemove
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptySet()
    )

    private val _state = MutableStateFlow(RomUiState())
    val state: StateFlow<RomUiState> = _state.asStateFlow()

    init {
        load()
        viewModelScope.launch {
            val s = settingsRepo.settings.first()
            if (s.notificationsEnabled) {
                WorkScheduler.schedule(application, s.checkIntervalMinutes)
            }
        }
    }

    fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            repository.fetchDevices()
                .onSuccess { devices ->
                    _state.value = _state.value.copy(
                        devices = devices,
                        loading = false,
                        error = null
                    )
                }
                .onFailure { e ->
                    _state.value = _state.value.copy(
                        loading = false,
                        error = e.message ?: "Unknown error"
                    )
                }
        }
    }

    fun selectPost(post: RomPost?) {
        _state.value = _state.value.copy(selectedPost = post)
    }

    fun follow(romKey: String, currentVersion: String, displayName: String) {
        _pendingUnfollowed.value = _pendingUnfollowed.value - romKey
        _pendingFollowed.value = _pendingFollowed.value + romKey
        viewModelScope.launch {
            try {
                followRepo.follow(romKey, currentVersion, displayName)
            } finally {
                _pendingFollowed.value = _pendingFollowed.value - romKey
            }
        }
    }

    fun unfollow(romKey: String) {
        _pendingFollowed.value = _pendingFollowed.value - romKey
        _pendingUnfollowed.value = _pendingUnfollowed.value + romKey
        viewModelScope.launch {
            try {
                followRepo.unfollow(romKey)
            } finally {
                _pendingUnfollowed.value = _pendingUnfollowed.value - romKey
            }
        }
    }

    /** Run the update checker once now (for followed ROMs). */
    fun runCheckNow() {
        WorkScheduler.runCheckNow(getApplication())
    }
}
