package com.JochemKuipers.irawansupdatechecker.worker

/**
 * Returns true if [newVer] is strictly newer than [oldVer] (e.g. 3.7 > 3.6, 12.7 > 12.5).
 */
fun isNewerVersion(newVer: String, oldVer: String): Boolean {
    if (newVer == oldVer) return false
    val newParts = newVer.split(".").map { it.toIntOrNull() ?: 0 }
    val oldParts = oldVer.split(".").map { it.toIntOrNull() ?: 0 }
    for (i in 0 until maxOf(newParts.size, oldParts.size)) {
        val n = newParts.getOrElse(i) { 0 }
        val o = oldParts.getOrElse(i) { 0 }
        if (n > o) return true
        if (n < o) return false
    }
    return false
}
