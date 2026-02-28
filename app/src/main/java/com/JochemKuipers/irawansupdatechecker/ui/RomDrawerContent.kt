package com.JochemKuipers.irawansupdatechecker.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.JochemKuipers.irawansupdatechecker.data.DeviceItem
import com.JochemKuipers.irawansupdatechecker.data.RomEntry
import com.JochemKuipers.irawansupdatechecker.data.RomPost

@Composable
fun RomDrawerContent(
    devices: List<DeviceItem>,
    selectedPost: RomPost?,
    onPostSelected: (RomPost) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandedDevices by remember { mutableStateOf(emptySet<String>()) }
    val expandedRoms = remember { mutableStateListOf<String>() }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "Irawan's ROMs",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        devices.forEach { device ->
            DeviceRow(
                device = device,
                isExpanded = device.codename in expandedDevices,
                expandedRoms = expandedRoms,
                selectedPost = selectedPost,
                onPostSelected = onPostSelected,
                onToggleDevice = {
                    expandedDevices = if (device.codename in expandedDevices) expandedDevices - device.codename
                    else expandedDevices + device.codename
                },
                onToggleRom = { key ->
                    if (key in expandedRoms) expandedRoms.remove(key)
                    else expandedRoms.add(key)
                }
            )
        }
    }
}

@Composable
private fun DeviceRow(
    device: DeviceItem,
    isExpanded: Boolean,
    expandedRoms: List<String>,
    selectedPost: RomPost?,
    onPostSelected: (RomPost) -> Unit,
    onToggleDevice: () -> Unit,
    onToggleRom: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggleDevice)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "${device.deviceName} (${device.codename})",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        if (isExpanded) {
            device.roms.forEach { rom ->
                val romKey = "${device.codename}|${rom.name}"
                key(romKey) {
                    RomRow(
                        rom = rom,
                        isExpanded = romKey in expandedRoms,
                        selectedPost = selectedPost,
                        onPostSelected = onPostSelected,
                        onToggle = { onToggleRom(romKey) }
                    )
                }
            }
        }
    }
}

@Composable
private fun RomRow(
    rom: RomEntry,
    isExpanded: Boolean,
    selectedPost: RomPost?,
    onPostSelected: (RomPost) -> Unit,
    onToggle: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .clickable(onClick = onToggle)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = rom.name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        if (isExpanded) {
            rom.updates.forEach { post ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (selectedPost?.url == post.url)
                                Modifier.background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                            else Modifier
                        )
                        .clickable(onClick = { onPostSelected(post) })
                        .padding(start = 48.dp, end = 16.dp, top = 6.dp, bottom = 6.dp)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "v${post.version}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = post.date,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
