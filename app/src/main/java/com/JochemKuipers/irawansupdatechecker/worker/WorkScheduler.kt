package com.JochemKuipers.irawansupdatechecker.worker

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

private const val WORK_NAME = "rom_update_check"

object WorkScheduler {

    fun schedule(context: Context, intervalMinutes: Int) {
        val request = PeriodicWorkRequestBuilder<UpdateCheckerWorker>(
            repeatInterval = intervalMinutes.toLong().coerceAtLeast(15),
            repeatIntervalTimeUnit = TimeUnit.MINUTES
        ).build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }
}
