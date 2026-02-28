package com.JochemKuipers.irawansupdatechecker.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.io.IOException

private const val BASE_URL = "https://dodyirawan85.github.io/"

class RomRepository {

    suspend fun fetchDevices(): Result<List<DeviceItem>> = withContext(Dispatchers.IO) {
        try {
            val doc = Jsoup.connect(BASE_URL)
                .userAgent("IrawansUpdateChecker/1.0")
                .timeout(15_000)
                .get()

            val posts = mutableListOf<RomPost>()

            doc.select("article.post").forEach { article ->
                val titleEl = article.selectFirst("h2.post-title a") ?: return@forEach
                val title = titleEl.text().trim()
                val url = titleEl.attr("abs:href").ifBlank { titleEl.attr("href")?.let { BASE_URL.trimEnd('/') + it } ?: "" }
                val dateEl = article.selectFirst("ul.post-meta time[datetime]")
                val date = when {
                    dateEl?.attr("datetime")?.startsWith("2") == true -> dateEl.attr("datetime").take(10)
                    else -> dateEl?.text()?.trim() ?: ""
                }

                val parsed = TitleParser.parse(title) ?: return@forEach
                posts += RomPost(
                    title = title,
                    url = url,
                    date = date,
                    romName = parsed.romName,
                    version = parsed.version,
                    deviceName = parsed.deviceName,
                    codename = parsed.codename
                )
            }

            val devices = buildDeviceHierarchy(posts)
            Result.success(devices)
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun buildDeviceHierarchy(posts: List<RomPost>): List<DeviceItem> {
        // Group by codename -> device name, then by rom name, then sort versions by date desc
        val byDevice = posts.groupBy { it.codename }
        return byDevice.map { (codename, devicePosts) ->
            val deviceName = devicePosts.first().deviceName
            val byRom = devicePosts.groupBy { it.romName }.map { (name, romPosts) ->
                RomEntry(
                    name = name,
                    updates = romPosts.sortedByDescending { it.date }
                )
            }.sortedBy { it.name }
            DeviceItem(
                codename = codename,
                deviceName = deviceName,
                roms = byRom
            )
        }.sortedWith(compareBy({ it.deviceName }, { it.codename }))
    }
}
