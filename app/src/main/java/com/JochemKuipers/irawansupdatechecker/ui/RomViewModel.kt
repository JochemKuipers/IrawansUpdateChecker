package com.JochemKuipers.irawansupdatechecker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.JochemKuipers.irawansupdatechecker.data.DeviceItem
import com.JochemKuipers.irawansupdatechecker.data.RomPost
import com.JochemKuipers.irawansupdatechecker.data.RomRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RomUiState(
    val devices: List<DeviceItem> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null,
    val selectedPost: RomPost? = null
)

class RomViewModel(
    private val repository: RomRepository = RomRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(RomUiState())
    val state: StateFlow<RomUiState> = _state.asStateFlow()

    init {
        load()
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
}
