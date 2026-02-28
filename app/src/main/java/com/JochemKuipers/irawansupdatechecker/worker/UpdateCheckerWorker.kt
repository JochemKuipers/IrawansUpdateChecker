package com.JochemKuipers.irawansupdatechecker.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.JochemKuipers.irawansupdatechecker.data.FollowRepository
import com.JochemKuipers.irawansupdatechecker.data.RomPost
import com.JochemKuipers.irawansupdatechecker.data.RomRepository
import com.JochemKuipers.irawansupdatechecker.data.SettingsRepository
import kotlinx.coroutines.flow.first

class UpdateCheckerWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val settingsRepo = SettingsRepository(applicationContext)
        val followRepo = FollowRepository(applicationContext)
        val romRepo = RomRepository()

        val settings = settingsRepo.settings.first()
        if (!settings.notificationsEnabled) return Result.success()

        val followed = followRepo.followedRoms.first()
        if (followed.isEmpty()) return Result.success()

        val fetchResult = romRepo.fetchDevices()
        val devices = fetchResult.getOrNull() ?: return Result.retry()

        // Build map romKey -> latest RomPost from fetched data
        val latestByRomKey = mutableMapOf<String, RomPost>()
        devices.forEach { device ->
            device.roms.forEach { rom ->
                rom.updates.firstOrNull()?.let { latest ->
                    val key = "${device.codename}|${rom.name}"
                    latestByRomKey[key] = latest
                }
            }
        }

        var notificationsShown = 0
        for (fr in followed) {
            val current = latestByRomKey[fr.romKey] ?: continue
            if (isNewerVersion(current.version, fr.lastSeenVersion)) {
                NotificationHelper.showUpdateNotification(
                    context = applicationContext,
                    romDisplayName = fr.displayName,
                    newVersion = current.version,
                    notificationId = fr.romKey.hashCode()
                )
                followRepo.updateLastSeen(fr.romKey, current.version, fr.displayName)
                notificationsShown++
            }
        }

        return Result.success()
    }
}
