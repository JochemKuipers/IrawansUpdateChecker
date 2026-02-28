package com.JochemKuipers.irawansupdatechecker.data

/**
 * Single ROM post from the site.
 * Title format: "{RomName} {version} [optional] For {Device Name} ({codename})"
 */
data class RomPost(
    val title: String,
    val url: String,
    val date: String,
    val romName: String,
    val version: String,
    val deviceName: String,
    val codename: String
)
