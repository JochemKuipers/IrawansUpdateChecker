package com.JochemKuipers.irawansupdatechecker.data

import java.util.regex.Pattern

/**
 * Parses post title in the form:
 * "{RomName} {version} [optional words] For {Device Name} ({codename})"
 */
object TitleParser {

    // Group 1: rom name, 2: version, 3: device name, 4: codename
    private val TITLE_PATTERN = Pattern.compile(
        "^(.+?)\\s+([\\d.]+)(?:\\s+.+?)?\\s+[Ff]or\\s+(.+?)\\s*\\(([^)]+)\\)\\s*$"
    )

    fun parse(title: String): ParsedTitle? {
        val m = TITLE_PATTERN.matcher(title.trim())
        if (!m.matches()) return null
        return ParsedTitle(
            romName = m.group(1)!!.trim(),
            version = m.group(2)!!.trim(),
            deviceName = m.group(3)!!.trim(),
            codename = m.group(4)!!.trim()
        )
    }

    data class ParsedTitle(
        val romName: String,
        val version: String,
        val deviceName: String,
        val codename: String
    )
}
