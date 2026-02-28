package com.JochemKuipers.irawansupdatechecker.worker

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.testing.WorkManagerTestInitHelper
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Unit tests for [WorkScheduler] (run with testDebugUnitTest).
 * Uses Robolectric for Android context; verifies check-interval scheduling.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class WorkSchedulerTest {

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        WorkManagerTestInitHelper.initializeTestWorkManager(context)
    }

    @Test
    fun schedule_enqueuesUniquePeriodicWork() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        WorkScheduler.schedule(context, 60)

        val workManager = WorkManager.getInstance(context)
        val workInfos = workManager.getWorkInfosForUniqueWork("rom_update_check").get()

        assertTrue("Expected one periodic work to be enqueued", workInfos.size >= 1)
        assertTrue(
            "Work should be enqueued or running",
            workInfos.any { it.state == WorkInfo.State.ENQUEUED || it.state == WorkInfo.State.RUNNING }
        )
    }

    @Test
    fun cancel_removesWork() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        WorkScheduler.schedule(context, 120)
        var workInfos = WorkManager.getInstance(context).getWorkInfosForUniqueWork("rom_update_check").get()
        assertTrue(workInfos.isNotEmpty())

        WorkScheduler.cancel(context)
        workInfos = WorkManager.getInstance(context).getWorkInfosForUniqueWork("rom_update_check").get()
        assertTrue("Work should be cancelled", workInfos.isEmpty() || workInfos.all { it.state == WorkInfo.State.CANCELLED })
    }
}
