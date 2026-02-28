package com.JochemKuipers.irawansupdatechecker.worker

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Unit tests for [NotificationHelper] (run with testDebugUnitTest).
 * Uses Robolectric for Android context; verifies channel and notification don't crash.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class NotificationHelperTest {

    @Test
    fun ensureChannel_doesNotCrash() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        NotificationHelper.ensureChannel(context)
    }

    @Test
    fun showUpdateNotification_doesNotCrash() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        NotificationHelper.ensureChannel(context)
        NotificationHelper.showUpdateNotification(
            context = context,
            romDisplayName = "LunarisAOSP for Xiaomi Pad 7 Pro",
            newVersion = "3.7",
            notificationId = 1
        )
    }
}
