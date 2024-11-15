package de.astronarren.allsky.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UpdateRepository {
    private val updateService = Retrofit.Builder()
        .baseUrl("https://api.github.com/repos/acocalypso/allskyviewer-companion/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(UpdateService::class.java)

    suspend fun checkForUpdate(currentVersion: String): UpdateInfo? {
        return withContext(Dispatchers.IO) {
            try {
                val release = updateService.getLatestRelease()
                val latestVersion = release.tag_name.removePrefix("v")
                
                if (isNewerVersion(latestVersion, currentVersion)) {
                    val apkAsset = release.assets.find { it.name.endsWith(".apk") }
                    if (apkAsset != null) {
                        UpdateInfo(
                            latestVersion = latestVersion,
                            downloadUrl = apkAsset.browser_download_url,
                            releaseNotes = release.body,
                            apkSize = apkAsset.size
                        )
                    } else null
                } else null
            } catch (e: Exception) {
                println("Debug: Update check failed: ${e.message}")
                null
            }
        }
    }

    private fun isNewerVersion(latest: String, current: String): Boolean {
        val latestParts = latest.split(".").map { it.toInt() }
        val currentParts = current.split(".").map { it.toInt() }
        
        for (i in 0..2) {
            val latestPart = latestParts.getOrNull(i) ?: 0
            val currentPart = currentParts.getOrNull(i) ?: 0
            
            if (latestPart > currentPart) return true
            if (latestPart < currentPart) return false
        }
        return false
    }
}

data class UpdateInfo(
    val latestVersion: String,
    val downloadUrl: String,
    val releaseNotes: String,
    val apkSize: Long
) 