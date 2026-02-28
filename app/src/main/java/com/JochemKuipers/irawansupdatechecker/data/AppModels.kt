package com.JochemKuipers.irawansupdatechecker.data

/**
 * Device category (sidebar L1).
 */
data class DeviceItem(
    val codename: String,
    val deviceName: String,
    val roms: List<RomEntry>
)

/**
 * ROM subcategory for a device (sidebar L2).
 */
data class RomEntry(
    val name: String,
    val updates: List<RomPost>  // versions, newest first
)
