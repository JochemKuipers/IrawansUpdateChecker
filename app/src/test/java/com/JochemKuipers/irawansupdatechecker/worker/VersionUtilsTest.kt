package com.JochemKuipers.irawansupdatechecker.worker

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for [isNewerVersion] used by [UpdateCheckerWorker] to decide when to show notifications.
 */
class VersionUtilsTest {

    @Test
    fun sameVersion_returnsFalse() {
        assertFalse(isNewerVersion("3.7", "3.7"))
        assertFalse(isNewerVersion("12.5", "12.5"))
    }

    @Test
    fun newerMinor_returnsTrue() {
        assertTrue(isNewerVersion("3.7", "3.6"))
        assertTrue(isNewerVersion("12.7", "12.5"))
    }

    @Test
    fun olderVersion_returnsFalse() {
        assertFalse(isNewerVersion("3.6", "3.7"))
        assertFalse(isNewerVersion("12.5", "12.7"))
    }

    @Test
    fun newerMajor_returnsTrue() {
        assertTrue(isNewerVersion("4.0", "3.7"))
        assertTrue(isNewerVersion("2.3", "1.9"))
    }

    @Test
    fun moreSegments_newer() {
        assertTrue(isNewerVersion("3.7.1", "3.7"))
        assertFalse(isNewerVersion("3.7", "3.7.1"))
    }

    @Test
    fun nonNumeric_treatedAsZero() {
        assertTrue(isNewerVersion("3.7", "3.x"))
        assertTrue(isNewerVersion("3.1", "3.0"))
    }
}
